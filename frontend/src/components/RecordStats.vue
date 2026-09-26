<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import type { TableColumnCtx } from 'element-plus';
import ReportToolbar from './ReportToolbar.vue';
import {
  EXPORT_EMPTY_NOTICE,
  EXPORT_SUCCESS_NOTICE,
  MONTH_PREFIX_LENGTH,
  REPORT_MODE,
  SUMMARY_LABEL,
} from '../constants/report.constants';
import { exportWorkReport } from '../services/export.service';
import {
  dailySelectionForMonth,
  defaultReportSelection,
  describeEmptyReport,
  describeReportRange,
  filterRecordsByRange,
  summarizeRecords,
} from '../services/report.service';
import { logger } from '../logger/logger';
import type { ReportMode, WorkRecord } from '../types/domain';

const props = defineProps<{ records: WorkRecord[] }>();

const mode = ref<ReportMode>(REPORT_MODE.DAILY);
const selection = ref(defaultReportSelection(props.records, mode.value));
const exporting = ref(false);

const filtered = computed(() => filterRecordsByRange(props.records, mode.value, selection.value));
const totals = computed(() => summarizeRecords(filtered.value));
const rangeText = computed(() => describeReportRange(mode.value, selection.value, filtered.value.length));
const emptyText = computed(() => describeEmptyReport(mode.value, selection.value));

watch(mode, (next) => {
  selection.value =
    next === REPORT_MODE.MONTHLY
      ? selection.value.slice(0, MONTH_PREFIX_LENGTH)
      : dailySelectionForMonth(props.records, selection.value);
});

const summaryMethod = ({ columns }: { columns: TableColumnCtx<WorkRecord>[] }): string[] =>
  columns.map((column) => {
    switch (column.property) {
      case 'workDate':
        return SUMMARY_LABEL;
      case 'actualHours':
        return String(totals.value.actualHours);
      case 'fuelLiters':
        return String(totals.value.fuelLiters);
      case 'areaMu':
        return String(totals.value.areaMu);
      case 'fuelCost':
        return String(totals.value.fuelCost);
      default:
        return '';
    }
  });

const handleExport = async () => {
  if (!filtered.value.length) {
    ElMessage.info(emptyText.value);
    return;
  }
  exporting.value = true;
  try {
    const result = await exportWorkReport(mode.value, selection.value);
    if (result === 'empty') {
      ElMessage.info(EXPORT_EMPTY_NOTICE);
    } else {
      ElMessage.success(EXPORT_SUCCESS_NOTICE);
    }
  } catch (err) {
    logger.error('report export failed', err);
    ElMessage.error(err instanceof Error ? err.message : '导出失败');
  } finally {
    exporting.value = false;
  }
};
</script>

<template>
  <section class="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
    <div class="mb-3 flex flex-wrap items-center justify-between gap-3">
      <div>
        <h2 class="text-lg font-black">作业统计报表</h2>
        <p class="mt-1 text-xs text-slate-500">{{ rangeText }}</p>
      </div>
      <ReportToolbar
        v-model:mode="mode"
        v-model:selection="selection"
        :can-export="filtered.length > 0"
        :exporting="exporting"
        @export="handleExport"
      />
    </div>

    <el-empty v-if="!filtered.length" :description="emptyText" :image-size="80" />

    <el-table v-else :data="filtered" size="small" show-summary :summary-method="summaryMethod">
      <el-table-column prop="workDate" label="日期" width="110" />
      <el-table-column prop="machineCode" label="农机" width="120" />
      <el-table-column prop="driverName" label="驾驶员" width="90" />
      <el-table-column prop="taskType" label="类型" width="90" />
      <el-table-column prop="actualHours" label="工时" width="80" />
      <el-table-column prop="fuelLiters" label="油耗/L" width="90" />
      <el-table-column prop="areaMu" label="面积/亩" width="90" />
      <el-table-column prop="fuelCost" label="油耗成本" />
    </el-table>
  </section>
</template>

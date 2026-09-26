<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, ref, watch } from 'vue';
import { REPORT_EMPTY_HINT, REPORT_PERIOD_OPTIONS } from '../constants/app.constants';
import { logger } from '../logger/logger';
import { downloadWorkReportCsv, fetchWorkReport } from '../services/storage.service';
import type { ReportPeriod, WorkRecord, WorkReport } from '../types/domain';

const props = defineProps<{ records: WorkRecord[] }>();

const period = ref<ReportPeriod>('daily');
const selectedValue = ref('');
const report = ref<WorkReport>();
const emptyReason = ref('');
const loading = ref(false);
const exporting = ref(false);

const pickerType = computed(() => (period.value === 'daily' ? 'date' : 'month'));
const pickerFormat = computed(() => (period.value === 'daily' ? 'YYYY-MM-DD' : 'YYYY-MM'));
const hasRows = computed(() => (report.value?.rows.length ?? 0) > 0);

const latestWorkDate = () => {
  const dates = props.records.map((record) => record.workDate).sort();
  return dates.length > 0 ? dates[dates.length - 1] : new Date().toISOString().slice(0, 10);
};

const defaultValue = (target: ReportPeriod) => {
  const latest = latestWorkDate();
  return target === 'daily' ? latest : latest.slice(0, 7);
};

const loadReport = async () => {
  if (!selectedValue.value) {
    return;
  }
  loading.value = true;
  try {
    report.value = await fetchWorkReport(period.value, selectedValue.value);
    emptyReason.value = '';
    logger.info('work report loaded', period.value, selectedValue.value);
  } catch (err) {
    report.value = undefined;
    emptyReason.value = err instanceof Error ? err.message : REPORT_EMPTY_HINT[period.value];
  } finally {
    loading.value = false;
  }
};

const handleExport = async () => {
  if (!hasRows.value) {
    ElMessage.warning(emptyReason.value || REPORT_EMPTY_HINT[period.value]);
    return;
  }
  exporting.value = true;
  try {
    await downloadWorkReportCsv(period.value, selectedValue.value);
    ElMessage.success('CSV 已导出，包含当前范围合计');
  } catch (err) {
    ElMessage.warning(err instanceof Error ? err.message : '导出失败');
  } finally {
    exporting.value = false;
  }
};

const summaryCells = () => {
  const totals = report.value?.totals;
  if (!totals) {
    return [];
  }
  return ['合计', '—', `${totals.actualHours}`, `${totals.fuelLiters}`, `${totals.areaMu}`, `${totals.fuelCost}`];
};

watch(period, (target) => {
  selectedValue.value = defaultValue(target);
});
watch(selectedValue, loadReport);

onMounted(() => {
  selectedValue.value = defaultValue(period.value);
});
</script>

<template>
  <section class="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
    <div class="mb-3 flex flex-wrap items-center justify-between gap-3">
      <h2 class="text-lg font-black">作业统计报表</h2>
      <div class="flex flex-wrap items-center gap-2">
        <el-radio-group v-model="period" size="small">
          <el-radio-button v-for="option in REPORT_PERIOD_OPTIONS" :key="option.value" :value="option.value">
            {{ option.label }}
          </el-radio-button>
        </el-radio-group>
        <el-date-picker
          v-model="selectedValue"
          :type="pickerType"
          :value-format="pickerFormat"
          :clearable="false"
          size="small"
          style="width: 150px"
        />
        <el-button size="small" type="primary" :loading="exporting" :disabled="!hasRows" @click="handleExport">
          导出 CSV
        </el-button>
      </div>
    </div>

    <el-skeleton v-if="loading" :rows="4" animated />

    <template v-else-if="report && hasRows">
      <p class="mb-2 text-sm text-slate-500">{{ report.rangeLabel }} · 按农机与驾驶员汇总</p>
      <el-table :data="report.rows" size="small" show-summary :summary-method="summaryCells">
        <el-table-column prop="machineCode" label="农机" width="130" />
        <el-table-column prop="driverName" label="驾驶员" width="90" />
        <el-table-column prop="actualHours" label="工时/h" width="90" />
        <el-table-column prop="fuelLiters" label="油耗/L" width="90" />
        <el-table-column prop="areaMu" label="面积/亩" width="90" />
        <el-table-column prop="fuelCost" label="油耗成本/元" />
      </el-table>

      <h3 class="mb-2 mt-4 text-sm font-bold text-slate-700">
        对应作业记录（{{ report.records.length }} 条）
      </h3>
      <el-table :data="report.records" size="small" max-height="260">
        <el-table-column prop="workDate" label="日期" width="110" />
        <el-table-column prop="machineCode" label="农机" width="130" />
        <el-table-column prop="driverName" label="驾驶员" width="90" />
        <el-table-column prop="taskType" label="类型" width="80" />
        <el-table-column prop="actualHours" label="工时/h" width="80" />
        <el-table-column prop="fuelLiters" label="油耗/L" width="80" />
        <el-table-column prop="areaMu" label="面积/亩" width="80" />
        <el-table-column prop="fuelCost" label="油耗成本/元" />
      </el-table>
    </template>

    <el-empty v-else :description="emptyReason || REPORT_EMPTY_HINT[period]" />
  </section>
</template>

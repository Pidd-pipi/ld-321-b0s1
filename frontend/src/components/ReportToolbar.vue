<script setup lang="ts">
import { computed } from 'vue';
import {
  DAILY_DATE_FORMAT,
  MONTH_DATE_FORMAT,
  REPORT_EXPORT_LABEL,
  REPORT_MODE,
  REPORT_MODE_OPTIONS,
} from '../constants/report.constants';
import type { ReportMode } from '../types/domain';

const props = defineProps<{
  mode: ReportMode;
  selection: string;
  canExport: boolean;
  exporting: boolean;
}>();

const emit = defineEmits<{
  'update:mode': [value: ReportMode];
  'update:selection': [value: string];
  export: [];
}>();

const pickerType = computed(() => (props.mode === REPORT_MODE.MONTHLY ? 'month' : 'date'));
const valueFormat = computed(() =>
  props.mode === REPORT_MODE.MONTHLY ? MONTH_DATE_FORMAT : DAILY_DATE_FORMAT,
);
const pickerPlaceholder = computed(() =>
  props.mode === REPORT_MODE.MONTHLY ? '选择月份' : '选择作业日期',
);
</script>

<template>
  <div class="flex flex-wrap items-center gap-2">
    <el-radio-group
      :model-value="mode"
      size="small"
      @update:model-value="emit('update:mode', $event as ReportMode)"
    >
      <el-radio-button v-for="option in REPORT_MODE_OPTIONS" :key="option.value" :value="option.value">
        {{ option.label }}
      </el-radio-button>
    </el-radio-group>
    <el-date-picker
      :key="mode"
      :model-value="selection"
      :type="pickerType"
      :value-format="valueFormat"
      :placeholder="pickerPlaceholder"
      :clearable="false"
      size="small"
      class="w-36"
      @update:model-value="emit('update:selection', $event as string)"
    />
    <el-button
      size="small"
      type="primary"
      :disabled="!canExport"
      :loading="exporting"
      @click="emit('export')"
    >
      {{ REPORT_EXPORT_LABEL }}
    </el-button>
  </div>
</template>

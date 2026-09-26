export const APP_NAME = '农机调度管理系统';
export const API_BASE = '/api';
export const STATUS_COLORS: Record<string, string> = {
  空闲: 'success',
  作业中: 'warning',
  维修中: 'danger',
  待派单: 'info',
  已派单: 'warning',
  已完成: 'success',
};
export const REPORT_PERIOD_OPTIONS = [
  { value: 'daily', label: '日报' },
  { value: 'monthly', label: '月报' },
] as const;
export const REPORT_EMPTY_HINT: Record<string, string> = {
  daily: '所选作业日期没有作业记录，请切换其他日期',
  monthly: '所选月份没有作业记录，请切换其他月份',
};
export const REPORT_LOAD_FAILED = '无法加载作业统计报表';
export const REPORT_EXPORT_FAILED = '导出失败，请稍后重试';

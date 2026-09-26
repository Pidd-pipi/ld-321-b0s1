export const REPORT_MODE = { DAILY: 'daily', MONTHLY: 'monthly' } as const;

export const REPORT_MODE_OPTIONS = [
  { value: REPORT_MODE.DAILY, label: '日报' },
  { value: REPORT_MODE.MONTHLY, label: '月报' },
] as const;

export const REPORT_MODE_LABELS: Record<string, string> = {
  [REPORT_MODE.DAILY]: '日报',
  [REPORT_MODE.MONTHLY]: '月报',
};

export const DAILY_DATE_FORMAT = 'YYYY-MM-DD';
export const MONTH_DATE_FORMAT = 'YYYY-MM';
export const MONTH_PREFIX_LENGTH = 7;

export const SUMMARY_LABEL = '合计';
export const REPORT_EXPORT_LABEL = '导出 CSV';
export const EXPORT_SUCCESS_NOTICE = 'CSV 已导出，内容与当前报表范围一致并包含合计行';
export const EXPORT_EMPTY_NOTICE = '当前范围没有作业记录，未生成导出文件';
export const EMPTY_REPORT_HINT = '可切换其他时间范围，或等待驾驶员提交作业记录';

export const CSV_FALLBACK_NAMES: Record<string, string> = {
  [REPORT_MODE.DAILY]: '作业日报',
  [REPORT_MODE.MONTHLY]: '作业月报',
};

export const HTTP_STATUS_NO_CONTENT = 204;

export const ROUND_PRECISION = { HOURS: 1, FUEL: 1, AREA: 1, COST: 2 } as const;

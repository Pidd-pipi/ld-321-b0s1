import {
  EMPTY_REPORT_HINT,
  MONTH_PREFIX_LENGTH,
  REPORT_MODE,
  REPORT_MODE_LABELS,
  ROUND_PRECISION,
} from '../constants/report.constants';
import type { ReportMode, WorkRecord, WorkRecordTotals } from '../types/domain';

export const filterRecordsByRange = (
  records: WorkRecord[],
  mode: ReportMode,
  selection: string,
): WorkRecord[] =>
  records.filter((record) =>
    mode === REPORT_MODE.MONTHLY ? record.workDate.startsWith(selection) : record.workDate === selection,
  );

const roundTo = (value: number, precision: number): number => {
  const factor = 10 ** precision;
  return Math.round(value * factor) / factor;
};

export const summarizeRecords = (records: WorkRecord[]): WorkRecordTotals => ({
  count: records.length,
  actualHours: roundTo(
    records.reduce((sum, record) => sum + record.actualHours, 0),
    ROUND_PRECISION.HOURS,
  ),
  fuelLiters: roundTo(
    records.reduce((sum, record) => sum + record.fuelLiters, 0),
    ROUND_PRECISION.FUEL,
  ),
  areaMu: roundTo(
    records.reduce((sum, record) => sum + record.areaMu, 0),
    ROUND_PRECISION.AREA,
  ),
  fuelCost: roundTo(
    records.reduce((sum, record) => sum + record.fuelCost, 0),
    ROUND_PRECISION.COST,
  ),
});

const pad2 = (value: number): string => String(value).padStart(2, '0');

const todayString = (): string => {
  const now = new Date();
  return `${now.getFullYear()}-${pad2(now.getMonth() + 1)}-${pad2(now.getDate())}`;
};

const latestWorkDate = (records: WorkRecord[]): string => {
  if (!records.length) {
    return todayString();
  }
  const dates = records.map((record) => record.workDate).sort();
  return dates[dates.length - 1];
};

export const defaultReportSelection = (records: WorkRecord[], mode: ReportMode): string => {
  const latest = latestWorkDate(records);
  return mode === REPORT_MODE.MONTHLY ? latest.slice(0, MONTH_PREFIX_LENGTH) : latest;
};

export const dailySelectionForMonth = (records: WorkRecord[], month: string): string => {
  const inMonth = filterRecordsByRange(records, REPORT_MODE.MONTHLY, month);
  return inMonth.length ? latestWorkDate(inMonth) : defaultReportSelection(records, REPORT_MODE.DAILY);
};

export const describeReportRange = (mode: ReportMode, selection: string, count: number): string =>
  `${REPORT_MODE_LABELS[mode]} · ${selection} · 共 ${count} 条记录`;

export const describeEmptyReport = (mode: ReportMode, selection: string): string =>
  mode === REPORT_MODE.MONTHLY
    ? `${selection} 月没有作业记录，月报暂无数据可统计，${EMPTY_REPORT_HINT}`
    : `${selection} 当天没有作业记录，日报暂无数据可统计，${EMPTY_REPORT_HINT}`;

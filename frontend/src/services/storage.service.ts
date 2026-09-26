import { API_BASE, REPORT_EXPORT_FAILED, REPORT_LOAD_FAILED } from '../constants/app.constants';
import { AppException } from '../errors/AppException';
import { logger } from '../logger/logger';
import type { DashboardItem, FarmOverview, ReportPeriod, WorkReport } from '../types/domain';

export const fetchFarmOverview = async (): Promise<FarmOverview> => {
  const response = await fetch(`${API_BASE}/dashboard/overview`);
  if (!response.ok) {
    logger.error('overview request failed', response.status);
    throw new AppException('OVERVIEW_FAILED', '无法加载农机调度看板数据');
  }
  return response.json() as Promise<FarmOverview>;
};

export const dispatchTask = async (taskId: string) => {
  const response = await fetch(`${API_BASE}/tasks/${taskId}/dispatch`, { method: 'POST' });
  if (!response.ok) {
    throw new AppException('DISPATCH_FAILED', '派单失败');
  }
  return response.json();
};

const reportQuery = (period: ReportPeriod, value: string) =>
  `period=${period}&${period === 'daily' ? 'date' : 'month'}=${value}`;

const readErrorMessage = async (response: Response, fallback: string) => {
  const body = (await response.json().catch(() => null)) as { message?: string } | null;
  return body?.message ?? fallback;
};

export const fetchWorkReport = async (period: ReportPeriod, value: string): Promise<WorkReport> => {
  const response = await fetch(`${API_BASE}/reports/work?${reportQuery(period, value)}`);
  if (!response.ok) {
    logger.warn('work report request failed', response.status);
    throw new AppException('REPORT_FAILED', await readErrorMessage(response, REPORT_LOAD_FAILED));
  }
  return response.json() as Promise<WorkReport>;
};

export const downloadWorkReportCsv = async (period: ReportPeriod, value: string): Promise<void> => {
  const response = await fetch(`${API_BASE}/reports/work/export?${reportQuery(period, value)}`);
  if (!response.ok) {
    logger.warn('work report export rejected', response.status);
    throw new AppException('REPORT_EXPORT_FAILED', await readErrorMessage(response, REPORT_EXPORT_FAILED));
  }
  const blob = await response.blob();
  const disposition = response.headers.get('Content-Disposition') ?? '';
  const encoded = /filename\*=UTF-8''([^;]+)/i.exec(disposition);
  const plain = /filename="?([^";]+)"?/i.exec(disposition);
  const fileName = encoded ? decodeURIComponent(encoded[1]) : plain?.[1];
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = fileName ?? `work-report-${period}-${value}.csv`;
  link.click();
  URL.revokeObjectURL(link.href);
  logger.info('work report csv downloaded', link.download);
};

export const saveItems = (items: DashboardItem[]) => {
  localStorage.setItem('cyfarmsched.items', JSON.stringify(items));
};

import { API_BASE } from '../constants/app.constants';
import { CSV_FALLBACK_NAMES, HTTP_STATUS_NO_CONTENT } from '../constants/report.constants';
import { AppException } from '../errors/AppException';
import { logger } from '../logger/logger';
import type { ReportMode } from '../types/domain';

export type ExportResult = 'downloaded' | 'empty';

/**
 * 按当前报表范围（日报/月报 + 选中日期/月份）从后端导出 CSV。
 * 后端在范围内无记录时返回 204，此时不触发任何文件下载。
 */
export const exportWorkReport = async (mode: ReportMode, selection: string): Promise<ExportResult> => {
  const params = new URLSearchParams({ mode, date: selection });
  const response = await fetch(`${API_BASE}/reports/work/export?${params.toString()}`);
  if (response.status === HTTP_STATUS_NO_CONTENT) {
    logger.warn('export skipped: no records for', mode, selection);
    return 'empty';
  }
  if (!response.ok) {
    logger.error('export request failed', response.status);
    throw new AppException('EXPORT_FAILED', '导出失败，请稍后重试');
  }
  const blob = await response.blob();
  saveBlob(blob, resolveFileName(response, mode, selection));
  logger.info('report csv downloaded', mode, selection);
  return 'downloaded';
};

const resolveFileName = (response: Response, mode: ReportMode, selection: string): string => {
  const disposition = response.headers.get('Content-Disposition') ?? '';
  const match = /filename\*=UTF-8''([^;]+)/i.exec(disposition);
  if (match?.[1]) {
    return decodeURIComponent(match[1]);
  }
  return `${CSV_FALLBACK_NAMES[mode]}-${selection}.csv`;
};

const saveBlob = (blob: Blob, fileName: string): void => {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
};

import { API_BASE } from '../constants/app.constants';
import { AppException } from '../errors/AppException';
import { logger } from '../logger/logger';
import type { DashboardItem, FarmOverview } from '../types/domain';

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

export const saveItems = (items: DashboardItem[]) => {
  localStorage.setItem('cyfarmsched.items', JSON.stringify(items));
};

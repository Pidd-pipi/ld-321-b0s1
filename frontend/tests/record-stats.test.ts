// @vitest-environment jsdom
import { beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { mount, VueWrapper } from '@vue/test-utils';
import ElementPlus from 'element-plus';
import RecordStats from '../src/components/RecordStats.vue';
import type { WorkRecord } from '../src/types/domain';

const records: WorkRecord[] = [
  { id: 'r1', machineCode: 'NJ-2026-001', driverName: '周明', workDate: '2026-05-31', taskType: '耕地', actualHours: 8.5, fuelLiters: 76, areaMu: 156, fuelCost: 562.4 },
  { id: 'r2', machineCode: 'NJ-2026-002', driverName: '何燕', workDate: '2026-05-30', taskType: '播种', actualHours: 5.8, fuelLiters: 43, areaMu: 88, fuelCost: 318.2 },
  { id: 'r5', machineCode: 'NJ-2026-002', driverName: '何燕', workDate: '2026-05-31', taskType: '播种', actualHours: 4.6, fuelLiters: 39, areaMu: 82, fuelCost: 288.6 },
  { id: 'r7', machineCode: 'NJ-2026-001', driverName: '周明', workDate: '2026-06-02', taskType: '耕地', actualHours: 7.8, fuelLiters: 70, areaMu: 148, fuelCost: 518.0 },
];

const flushTable = () => new Promise((resolve) => setTimeout(resolve, 300));

const mountStats = async (): Promise<VueWrapper> => {
  const wrapper = mount(RecordStats, { props: { records }, global: { plugins: [ElementPlus] } });
  await flushTable();
  return wrapper;
};

beforeAll(() => {
  global.ResizeObserver = class {
    observe() {}
    unobserve() {}
    disconnect() {}
  } as unknown as typeof ResizeObserver;
});

beforeEach(() => {
  vi.restoreAllMocks();
});

describe('RecordStats', () => {
  it('defaults to daily report on the latest work date', async () => {
    const wrapper = await mountStats();
    expect(wrapper.text()).toContain('日报 · 2026-06-02 · 共 1 条记录');
    expect(wrapper.findAll('.el-table__row').length).toBe(1);
    expect(wrapper.text()).toContain('NJ-2026-001');
  });

  it('switches to monthly report and keeps only records of that month', async () => {
    const wrapper = await mountStats();
    await wrapper.findAll('input[type="radio"]')[1].setValue(true);
    await flushTable();
    expect(wrapper.text()).toContain('月报 · 2026-06 · 共 1 条记录');
    (wrapper.vm as unknown as { selection: string }).selection = '2026-05';
    await flushTable();
    expect(wrapper.text()).toContain('月报 · 2026-05 · 共 3 条记录');
    expect(wrapper.findAll('.el-table__row').length).toBe(3);
  });

  it('renders a totals row matching the selected range', async () => {
    const wrapper = await mountStats();
    await wrapper.findAll('input[type="radio"]')[1].setValue(true);
    (wrapper.vm as unknown as { selection: string }).selection = '2026-05';
    await flushTable();
    const footer = wrapper.find('.el-table__footer').text();
    expect(footer).toContain('合计');
    expect(footer).toContain('18.9');
    expect(footer).toContain('158');
    expect(footer).toContain('326');
    expect(footer).toContain('1169.2');
  });

  it('explains why empty and disables export when no records in range', async () => {
    const wrapper = await mountStats();
    (wrapper.vm as unknown as { selection: string }).selection = '2026-09-26';
    await flushTable();
    expect(wrapper.text()).toContain('2026-09-26 当天没有作业记录');
    expect(wrapper.find('.el-table').exists()).toBe(false);
    expect(wrapper.find('button.el-button--primary').attributes('disabled')).toBeDefined();
  });

  it('never calls the export API for an empty range', async () => {
    const fetchSpy = vi.spyOn(globalThis, 'fetch');
    const wrapper = await mountStats();
    (wrapper.vm as unknown as { selection: string }).selection = '2026-09-26';
    await flushTable();
    await wrapper.find('button.el-button--primary').trigger('click');
    expect(fetchSpy).not.toHaveBeenCalled();
  });

  it('exports CSV for the current selection and triggers a download', async () => {
    vi.stubGlobal('URL', Object.assign(URL, {
      createObjectURL: vi.fn(() => 'blob:mock'),
      revokeObjectURL: vi.fn(),
    }));
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response('csv', {
        status: 200,
        headers: { 'Content-Disposition': "attachment; filename*=UTF-8''%E4%BD%9C%E4%B8%9A%E6%97%A5%E6%8A%A5-2026-06-02.csv" },
      }),
    );
    const wrapper = await mountStats();
    await wrapper.find('button.el-button--primary').trigger('click');
    await flushTable();
    expect(fetchSpy).toHaveBeenCalledWith('/api/reports/work/export?mode=daily&date=2026-06-02');
    expect(URL.createObjectURL).toHaveBeenCalled();
  });

  it('treats HTTP 204 as empty and downloads nothing', async () => {
    vi.stubGlobal('URL', Object.assign(URL, {
      createObjectURL: vi.fn(() => 'blob:mock'),
      revokeObjectURL: vi.fn(),
    }));
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response(null, { status: 204 }));
    const wrapper = await mountStats();
    (wrapper.vm as unknown as { selection: string }).selection = '2026-06-02';
    await flushTable();
    await wrapper.find('button.el-button--primary').trigger('click');
    await flushTable();
    expect(URL.createObjectURL).not.toHaveBeenCalled();
  });
});

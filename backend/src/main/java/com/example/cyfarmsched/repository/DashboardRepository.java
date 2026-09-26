package com.example.cyfarmsched.repository;

import com.example.cyfarmsched.constants.AppConstants;
import com.example.cyfarmsched.model.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DashboardRepository {
    public List<DashboardItem> findAll() {
        return List.of(
                new DashboardItem("module-1", "农机档案", "唯一编号、二维码、照片和状态筛选", "运行中", 92),
                new DashboardItem("module-2", "任务调度", "推荐空闲农机和驾驶员并支持派单", "运行中", 90),
                new DashboardItem("module-3", "轨迹地图", "实时位置、历史轨迹和地块边界", "运行中", 88),
                new DashboardItem("module-4", "作业统计", "日报、月报、油耗成本和面积统计", "运行中", 91),
                new DashboardItem("module-5", "保养提醒", "按工时和日历周期生成预警", "运行中", 87),
                new DashboardItem("module-6", "驾驶员", "证照、排班、评价和历史作业量", "运行中", 89),
                new DashboardItem("module-7", "调度看板", "待办、空闲、作业中和七日趋势", "运行中", 93));
    }

    public List<Machine> machines() {
        return List.of(
                new Machine("m1", "NJ-2026-001", "东方红 1804", "LX1804", "2023-03-12", 180, "北岭 1 号田",
                        AppConstants.STATUS_WORKING, "QR-NJ-001", "/assets/machine-tractor.jpg", 284.5, "春耕翻地"),
                new Machine("m2", "NJ-2026-002", "雷沃谷神收割机", "GE80S", "2022-09-18", 160, "南湾稻田",
                        AppConstants.STATUS_IDLE, "QR-NJ-002", "/assets/machine-harvester.jpg", 412.0, "可派单"),
                new Machine("m3", "NJ-2026-003", "中联履带拖拉机", "RK140", "2024-01-06", 140, "西坡旱地",
                        AppConstants.STATUS_REPAIR, "QR-NJ-003", "/assets/machine-crawler.jpg", 98.0, "液压检修"));
    }

    public List<FarmTask> tasks() {
        return List.of(
                new FarmTask("t1", "耕地", "北岭 1 号田", 180, 9.5, AppConstants.TASK_DISPATCHED,
                        "高", "NJ-2026-001", "周明", "今日 08:00-18:00"),
                new FarmTask("t2", "播种", "西坡旱地", 96, 6.0, AppConstants.TASK_PENDING,
                        "中", "NJ-2026-002", "何燕", "明日 07:30-14:00"),
                new FarmTask("t3", "施肥", "南湾稻田", 132, 5.5, AppConstants.TASK_PENDING,
                        "中", "NJ-2026-002", "刘强", "今日 14:00-20:00"),
                new FarmTask("t4", "收割", "东河麦田", 210, 11.0, AppConstants.TASK_DONE,
                        "高", "NJ-2026-004", "周明", "昨日 06:30-17:30"));
    }

    public List<TrackPoint> tracks() {
        return List.of(
                new TrackPoint("NJ-2026-001", "耕地", "2026-05-31 08:10", 116.316, 39.985, 8.2, "北岭 1 号田"),
                new TrackPoint("NJ-2026-001", "耕地", "2026-05-31 09:20", 116.322, 39.988, 7.6, "北岭 1 号田"),
                new TrackPoint("NJ-2026-001", "耕地", "2026-05-31 10:30", 116.329, 39.991, 8.8, "北岭 1 号田"),
                new TrackPoint("NJ-2026-002", "播种", "2026-05-30 15:00", 116.301, 39.972, 6.4, "西坡旱地"));
    }

    public List<WorkRecord> records() {
        return List.of(
                new WorkRecord("r1", "NJ-2026-001", "周明", "2026-05-31", "耕地", 8.5, 76, 156, 562.4),
                new WorkRecord("r2", "NJ-2026-002", "何燕", "2026-05-30", "播种", 5.8, 43, 88, 318.2),
                new WorkRecord("r3", "NJ-2026-004", "刘强", "2026-05-29", "收割", 10.2, 91, 203, 673.4),
                new WorkRecord("r4", "NJ-2026-001", "周明", "2026-05-28", "施肥", 6.4, 55, 122, 407.0));
    }

    public List<MaintenanceReminder> reminders() {
        return List.of(
                new MaintenanceReminder("s1", "NJ-2026-001", "100 小时换机油", "2026-06-05", 16, "warning", "2026-04-28 已更换滤芯"),
                new MaintenanceReminder("s2", "NJ-2026-003", "液压系统复检", "2026-06-02", 0, "danger", "2026-05-25 漏油维修"),
                new MaintenanceReminder("s3", "NJ-2026-002", "刀盘检查", "2026-06-12", 42, "normal", "2026-05-12 例行保养"));
    }

    public List<Driver> drivers() {
        return List.of(
                new Driver("d1", "周明", "A2-4101811990", "13800010001", "早班", "周日", 486, 4.8, "在岗"),
                new Driver("d2", "何燕", "B2-4101811992", "13800010002", "中班", "周三", 318, 4.7, "可派单"),
                new Driver("d3", "刘强", "A1-4101811988", "13800010003", "夜班", "周五", 402, 4.6, "休息"));
    }

    public DispatchBoard board() {
        return new DispatchBoard(
                5,
                1,
                List.of("NJ-2026-001 春耕翻地", "NJ-2026-004 麦田收割"),
                List.of("NJ-2026-003 液压复检", "NJ-2026-001 换机油"),
                List.of(96, 122, 138, 166, 203, 88, 156),
                List.of("5/25", "5/26", "5/27", "5/28", "5/29", "5/30", "5/31"));
    }

    public Map<String, Object> stats() {
        double area = records().stream().mapToDouble(WorkRecord::areaMu).sum();
        double hours = records().stream().mapToDouble(WorkRecord::actualHours).sum();
        double fuel = records().stream().mapToDouble(WorkRecord::fuelCost).sum();
        return Map.of("totalAreaMu", area, "totalHours", hours, "fuelCost", fuel);
    }
}

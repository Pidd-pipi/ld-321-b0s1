package com.example.cyfarmsched.constants;

public final class AppConstants {
    public static final String SERVICE_NAME = "cyfarmsched";
    public static final String STATUS_IDLE = "空闲";
    public static final String STATUS_WORKING = "作业中";
    public static final String STATUS_REPAIR = "维修中";
    public static final String TASK_PENDING = "待派单";
    public static final String TASK_DISPATCHED = "已派单";
    public static final String TASK_DONE = "已完成";
    public static final int MAINTENANCE_WARNING_DAYS = 7;
    public static final String PERIOD_DAILY = "daily";
    public static final String PERIOD_MONTHLY = "monthly";
    public static final String REPORT_TOTAL_LABEL = "合计";
    public static final String REPORT_CSV_HEADER = "农机,驾驶员,工时(h),油耗(L),面积(亩),油耗成本(元)";
    public static final String ERROR_REPORT_EMPTY = "REPORT_EMPTY";
    public static final String ERROR_REPORT_PARAM = "REPORT_PARAM_INVALID";

    private AppConstants() {
    }
}

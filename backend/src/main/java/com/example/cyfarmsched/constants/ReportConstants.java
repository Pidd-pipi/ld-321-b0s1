package com.example.cyfarmsched.constants;

public final class ReportConstants {
    public static final String MODE_DAILY = "daily";
    public static final String MODE_MONTHLY = "monthly";
    public static final String DAILY_DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    public static final String MONTH_DATE_REGEX = "\\d{4}-\\d{2}";
    public static final String REPORT_TYPE_DAILY = "作业日报";
    public static final String REPORT_TYPE_MONTHLY = "作业月报";
    public static final String CSV_BOM = "\uFEFF";
    public static final String CSV_HEADER = "作业日期,农机编号,驾驶员,作业类型,工时(小时),油耗(L),面积(亩),油耗成本(元)";
    public static final String CSV_TOTAL_LABEL = "合计";
    public static final String CSV_FILE_SUFFIX = ".csv";
    public static final String ERR_MODE_CODE = "REPORT_MODE_INVALID";
    public static final String ERR_MODE_MESSAGE = "报表类型只支持 daily（日报）或 monthly（月报）";
    public static final String ERR_DATE_CODE = "REPORT_DATE_INVALID";
    public static final String ERR_DATE_MESSAGE = "日期格式与报表类型不匹配：日报需 YYYY-MM-DD，月报需 YYYY-MM";

    private ReportConstants() {
    }
}

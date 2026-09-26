package com.example.cyfarmsched.service;

import com.example.cyfarmsched.constants.ReportConstants;
import com.example.cyfarmsched.model.WorkRecord;
import com.example.cyfarmsched.model.WorkReportTotals;

import java.math.BigDecimal;
import java.util.List;
import java.util.StringJoiner;

/**
 * 负责把作业记录渲染为 CSV 文本：带 BOM（便于 Excel 识别中文）、
 * 字段转义，并在末尾追加合计行。
 */
public final class CsvBuilder {
    private static final String LINE_BREAK = "\n";
    private static final char DELIMITER = ',';
    private static final char QUOTE = '"';

    private CsvBuilder() {
    }

    public static String build(List<WorkRecord> records, WorkReportTotals totals) {
        StringBuilder csv = new StringBuilder(ReportConstants.CSV_BOM)
                .append(ReportConstants.CSV_HEADER)
                .append(LINE_BREAK);
        for (WorkRecord record : records) {
            csv.append(dataRow(record)).append(LINE_BREAK);
        }
        csv.append(totalRow(totals)).append(LINE_BREAK);
        return csv.toString();
    }

    private static String dataRow(WorkRecord record) {
        return new StringJoiner(String.valueOf(DELIMITER))
                .add(escape(record.workDate()))
                .add(escape(record.machineCode()))
                .add(escape(record.driverName()))
                .add(escape(record.taskType()))
                .add(number(record.actualHours()))
                .add(number(record.fuelLiters()))
                .add(number(record.areaMu()))
                .add(number(record.fuelCost()))
                .toString();
    }

    private static String totalRow(WorkReportTotals totals) {
        return new StringJoiner(String.valueOf(DELIMITER))
                .add(escape(ReportConstants.CSV_TOTAL_LABEL))
                .add("").add("").add("")
                .add(number(totals.actualHours()))
                .add(number(totals.fuelLiters()))
                .add(number(totals.areaMu()))
                .add(number(totals.fuelCost()))
                .toString();
    }

    private static String number(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private static String escape(String value) {
        if (value.indexOf(DELIMITER) < 0 && value.indexOf(QUOTE) < 0
                && value.indexOf('\n') < 0 && value.indexOf('\r') < 0) {
            return value;
        }
        return QUOTE + value.replace(String.valueOf(QUOTE), "\"\"") + QUOTE;
    }
}

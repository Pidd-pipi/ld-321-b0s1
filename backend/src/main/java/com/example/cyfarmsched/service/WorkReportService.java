package com.example.cyfarmsched.service;

import com.example.cyfarmsched.constants.AppConstants;
import com.example.cyfarmsched.exceptions.BusinessException;
import com.example.cyfarmsched.logger.AppLogger;
import com.example.cyfarmsched.model.WorkRecord;
import com.example.cyfarmsched.model.WorkReport;
import com.example.cyfarmsched.model.WorkReportRow;
import com.example.cyfarmsched.repository.DashboardRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkReportService {
    private static final Logger log = AppLogger.getLogger(WorkReportService.class);
    private static final String CSV_BOM = "\uFEFF";

    private final DashboardRepository repo;

    public WorkReportService(DashboardRepository repo) {
        this.repo = repo;
    }

    public WorkReport buildReport(String period, String date, String month) {
        String value = resolveValue(period, date, month);
        List<WorkRecord> matched = filterRecords(period, value);
        if (matched.isEmpty()) {
            log.warn("work report empty: period={} value={}", period, value);
            throw new BusinessException(AppConstants.ERROR_REPORT_EMPTY,
                    rangeLabel(period, value) + "没有作业记录，未生成报表");
        }
        List<WorkReportRow> rows = aggregate(matched);
        return new WorkReport(period, rangeLabel(period, value), rows, totals(matched), matched);
    }

    public String csvFileName(String period, String date, String month) {
        return "work-report-" + period + "-" + resolveValue(period, date, month) + ".csv";
    }

    public String buildCsv(String period, String date, String month) {
        WorkReport report = buildReport(period, date, month);
        StringBuilder csv = new StringBuilder(CSV_BOM).append(AppConstants.REPORT_CSV_HEADER).append('\n');
        for (WorkReportRow row : report.rows()) {
            csv.append(csvLine(row.machineCode(), row.driverName(), row)).append('\n');
        }
        csv.append(csvLine(AppConstants.REPORT_TOTAL_LABEL, "", report.totals())).append('\n');
        return csv.toString();
    }

    private String resolveValue(String period, String date, String month) {
        if (AppConstants.PERIOD_DAILY.equals(period)) {
            return parseDate(date, "日报需要提供 date 参数，格式 yyyy-MM-dd");
        }
        if (AppConstants.PERIOD_MONTHLY.equals(period)) {
            return parseMonth(month, "月报需要提供 month 参数，格式 yyyy-MM");
        }
        throw new BusinessException(AppConstants.ERROR_REPORT_PARAM, "period 仅支持 daily 或 monthly");
    }

    private String parseDate(String value, String message) {
        try {
            return LocalDate.parse(value == null ? "" : value).toString();
        } catch (DateTimeParseException ex) {
            throw new BusinessException(AppConstants.ERROR_REPORT_PARAM, message);
        }
    }

    private String parseMonth(String value, String message) {
        try {
            return YearMonth.parse(value == null ? "" : value).toString();
        } catch (DateTimeParseException ex) {
            throw new BusinessException(AppConstants.ERROR_REPORT_PARAM, message);
        }
    }

    private List<WorkRecord> filterRecords(String period, String value) {
        return repo.records().stream()
                .filter(record -> AppConstants.PERIOD_DAILY.equals(period)
                        ? record.workDate().equals(value)
                        : record.workDate().startsWith(value))
                .toList();
    }

    private List<WorkReportRow> aggregate(List<WorkRecord> records) {
        Map<String, List<WorkRecord>> groups = new LinkedHashMap<>();
        for (WorkRecord record : records) {
            groups.computeIfAbsent(record.machineCode() + "|" + record.driverName(), key -> new ArrayList<>())
                    .add(record);
        }
        List<WorkReportRow> rows = new ArrayList<>();
        groups.forEach((key, group) -> {
            WorkRecord first = group.get(0);
            rows.add(new WorkReportRow(
                    first.machineCode(),
                    first.driverName(),
                    round(sumHours(group), 1),
                    round(group.stream().mapToDouble(WorkRecord::fuelLiters).sum(), 1),
                    round(group.stream().mapToDouble(WorkRecord::areaMu).sum(), 1),
                    round(group.stream().mapToDouble(WorkRecord::fuelCost).sum(), 2)));
        });
        return rows;
    }

    private WorkReportRow totals(List<WorkRecord> records) {
        return new WorkReportRow(
                AppConstants.REPORT_TOTAL_LABEL,
                "",
                round(sumHours(records), 1),
                round(records.stream().mapToDouble(WorkRecord::fuelLiters).sum(), 1),
                round(records.stream().mapToDouble(WorkRecord::areaMu).sum(), 1),
                round(records.stream().mapToDouble(WorkRecord::fuelCost).sum(), 2));
    }

    private double sumHours(List<WorkRecord> records) {
        return records.stream().mapToDouble(WorkRecord::actualHours).sum();
    }

    private String rangeLabel(String period, String value) {
        return AppConstants.PERIOD_DAILY.equals(period) ? value + " 日报" : value + " 月报";
    }

    private String csvLine(String machineCode, String driverName, WorkReportRow row) {
        return String.join(",",
                machineCode,
                driverName,
                String.valueOf(row.actualHours()),
                String.valueOf(row.fuelLiters()),
                String.valueOf(row.areaMu()),
                String.valueOf(row.fuelCost()));
    }

    private double round(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}

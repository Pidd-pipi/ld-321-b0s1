package com.example.cyfarmsched.service;

import com.example.cyfarmsched.constants.ReportConstants;
import com.example.cyfarmsched.exceptions.BusinessException;
import com.example.cyfarmsched.logger.AppLogger;
import com.example.cyfarmsched.model.CsvFile;
import com.example.cyfarmsched.model.WorkRecord;
import com.example.cyfarmsched.model.WorkReportTotals;
import com.example.cyfarmsched.repository.DashboardRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    private static final Logger log = AppLogger.getLogger(ReportService.class);
    private static final int TOTAL_SCALE = 2;

    private final DashboardRepository repo;

    public ReportService(DashboardRepository repo) {
        this.repo = repo;
    }

    public List<WorkRecord> filterRecords(String mode, String date) {
        validate(mode, date);
        return repo.records().stream()
                .filter(record -> inRange(record, mode, date))
                .toList();
    }

    /**
     * 生成当前范围的 CSV 报表；范围内没有记录时返回空，由控制层响应 204，
     * 避免导出只有表头的空文件。
     */
    public Optional<CsvFile> export(String mode, String date) {
        List<WorkRecord> records = filterRecords(mode, date);
        if (records.isEmpty()) {
            log.info("report export skipped, no records for mode={} date={}", mode, date);
            return Optional.empty();
        }
        WorkReportTotals totals = summarize(records);
        CsvFile file = new CsvFile(fileName(mode, date), CsvBuilder.build(records, totals));
        log.info("report exported: {} rows={}", file.fileName(), records.size());
        return Optional.of(file);
    }

    private void validate(String mode, String date) {
        if (!ReportConstants.MODE_DAILY.equals(mode) && !ReportConstants.MODE_MONTHLY.equals(mode)) {
            throw new BusinessException(ReportConstants.ERR_MODE_CODE, ReportConstants.ERR_MODE_MESSAGE);
        }
        String pattern = ReportConstants.MODE_MONTHLY.equals(mode)
                ? ReportConstants.MONTH_DATE_REGEX
                : ReportConstants.DAILY_DATE_REGEX;
        if (date == null || !date.matches(pattern)) {
            throw new BusinessException(ReportConstants.ERR_DATE_CODE, ReportConstants.ERR_DATE_MESSAGE);
        }
    }

    private boolean inRange(WorkRecord record, String mode, String date) {
        return ReportConstants.MODE_MONTHLY.equals(mode)
                ? record.workDate().startsWith(date)
                : record.workDate().equals(date);
    }

    private WorkReportTotals summarize(List<WorkRecord> records) {
        return new WorkReportTotals(
                records.size(),
                round(records.stream().mapToDouble(WorkRecord::actualHours).sum()),
                round(records.stream().mapToDouble(WorkRecord::fuelLiters).sum()),
                round(records.stream().mapToDouble(WorkRecord::areaMu).sum()),
                round(records.stream().mapToDouble(WorkRecord::fuelCost).sum()));
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(TOTAL_SCALE, RoundingMode.HALF_UP).doubleValue();
    }

    private String fileName(String mode, String date) {
        String type = ReportConstants.MODE_MONTHLY.equals(mode)
                ? ReportConstants.REPORT_TYPE_MONTHLY
                : ReportConstants.REPORT_TYPE_DAILY;
        return type + "-" + date + ReportConstants.CSV_FILE_SUFFIX;
    }
}

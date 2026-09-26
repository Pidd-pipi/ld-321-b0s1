package com.example.cyfarmsched.model;

import java.util.List;

public record WorkReport(
        String period,
        String rangeLabel,
        List<WorkReportRow> rows,
        WorkReportRow totals,
        List<WorkRecord> records) {
}

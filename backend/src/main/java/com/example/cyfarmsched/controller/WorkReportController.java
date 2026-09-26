package com.example.cyfarmsched.controller;

import com.example.cyfarmsched.model.WorkReport;
import com.example.cyfarmsched.service.WorkReportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/reports/work")
public class WorkReportController {
    private static final MediaType CSV_MEDIA_TYPE = MediaType.parseMediaType("text/csv; charset=UTF-8");

    private final WorkReportService service;

    public WorkReportController(WorkReportService service) {
        this.service = service;
    }

    @GetMapping
    public WorkReport report(@RequestParam String period,
                             @RequestParam(required = false) String date,
                             @RequestParam(required = false) String month) {
        return service.buildReport(period, date, month);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam String period,
                                         @RequestParam(required = false) String date,
                                         @RequestParam(required = false) String month) {
        byte[] body = service.buildCsv(period, date, month).getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(CSV_MEDIA_TYPE);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(service.csvFileName(period, date, month), StandardCharsets.UTF_8)
                .build());
        return ResponseEntity.ok().headers(headers).body(body);
    }
}

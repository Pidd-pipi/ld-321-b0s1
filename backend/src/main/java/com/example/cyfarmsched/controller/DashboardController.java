package com.example.cyfarmsched.controller;

import com.example.cyfarmsched.constants.AppConstants;
import com.example.cyfarmsched.model.CsvFile;
import com.example.cyfarmsched.service.DashboardService;
import com.example.cyfarmsched.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DashboardController {
    private static final MediaType CSV_MEDIA_TYPE = new MediaType("text", "csv", StandardCharsets.UTF_8);
    private static final String DISPOSITION_TEMPLATE = "attachment; filename*=UTF-8''%s";

    private final DashboardService service;
    private final ReportService reportService;

    public DashboardController(DashboardService service, ReportService reportService) {
        this.service = service;
        this.reportService = reportService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", AppConstants.SERVICE_NAME);
    }

    @GetMapping("/dashboard/overview")
    public Map<String, Object> overview() {
        return service.overview();
    }

    @PostMapping("/tasks/{taskId}/dispatch")
    public Map<String, Object> dispatch(@PathVariable String taskId) {
        return service.dispatch(taskId);
    }

    @GetMapping("/reports/work/export")
    public ResponseEntity<byte[]> exportReport(@RequestParam String mode, @RequestParam String date) {
        Optional<CsvFile> csv = reportService.export(mode, date);
        if (csv.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        String fileName = java.net.URLEncoder.encode(csv.get().fileName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format(DISPOSITION_TEMPLATE, fileName))
                .contentType(CSV_MEDIA_TYPE)
                .body(csv.get().content().getBytes(StandardCharsets.UTF_8));
    }
}

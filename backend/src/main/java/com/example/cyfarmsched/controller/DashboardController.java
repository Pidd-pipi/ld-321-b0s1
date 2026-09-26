package com.example.cyfarmsched.controller;

import com.example.cyfarmsched.constants.AppConstants;
import com.example.cyfarmsched.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
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
}

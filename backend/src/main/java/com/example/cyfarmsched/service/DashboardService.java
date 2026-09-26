package com.example.cyfarmsched.service;

import com.example.cyfarmsched.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DashboardService {
    private final DashboardRepository repo;

    public DashboardService(DashboardRepository repo) {
        this.repo = repo;
    }

    public Map<String, Object> overview() {
        return Map.of(
                "items", repo.findAll(),
                "machines", repo.machines(),
                "tasks", repo.tasks(),
                "tracks", repo.tracks(),
                "records", repo.records(),
                "maintenance", repo.reminders(),
                "drivers", repo.drivers(),
                "board", repo.board(),
                "stats", repo.stats());
    }

    public Map<String, Object> dispatch(String taskId) {
        return Map.of("taskId", taskId, "status", "已派单", "message", "系统已按空闲度和驾驶员排班完成推荐派单");
    }

    public Map<String, Object> exportReport() {
        return Map.of("fileName", "farm-work-report-2026-05.csv", "rows", repo.records().size(), "status", "ready");
    }
}

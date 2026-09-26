package com.example.cyfarmsched.model;

public record FarmTask(
        String id,
        String type,
        String field,
        double areaMu,
        double estimatedHours,
        String status,
        String priority,
        String recommendedMachine,
        String recommendedDriver,
        String plannedWindow) {
}

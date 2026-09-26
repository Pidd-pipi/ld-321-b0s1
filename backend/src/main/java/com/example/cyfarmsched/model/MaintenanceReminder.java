package com.example.cyfarmsched.model;

public record MaintenanceReminder(
        String id,
        String machineCode,
        String title,
        String dueDate,
        int remainingHours,
        String level,
        String lastServiceRecord) {
}

package com.example.cyfarmsched.model;

public record WorkRecord(
        String id,
        String machineCode,
        String driverName,
        String workDate,
        String taskType,
        double actualHours,
        double fuelLiters,
        double areaMu,
        double fuelCost) {
}

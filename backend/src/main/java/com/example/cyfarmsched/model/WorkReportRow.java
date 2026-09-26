package com.example.cyfarmsched.model;

public record WorkReportRow(
        String machineCode,
        String driverName,
        double actualHours,
        double fuelLiters,
        double areaMu,
        double fuelCost) {
}

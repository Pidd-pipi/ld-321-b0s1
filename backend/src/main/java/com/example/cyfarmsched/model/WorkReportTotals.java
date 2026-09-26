package com.example.cyfarmsched.model;

public record WorkReportTotals(
        int count,
        double actualHours,
        double fuelLiters,
        double areaMu,
        double fuelCost) {
}

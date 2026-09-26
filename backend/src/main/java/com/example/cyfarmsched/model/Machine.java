package com.example.cyfarmsched.model;

public record Machine(
        String id,
        String code,
        String name,
        String model,
        String purchasedAt,
        int horsepower,
        String field,
        String status,
        String qrCode,
        String photoUrl,
        double workHours,
        String currentTask) {
}

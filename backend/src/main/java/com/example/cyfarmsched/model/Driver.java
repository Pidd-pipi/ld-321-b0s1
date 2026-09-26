package com.example.cyfarmsched.model;

public record Driver(
        String id,
        String name,
        String licenseNo,
        String phone,
        String shift,
        String restDay,
        double monthAreaMu,
        double rating,
        String status) {
}

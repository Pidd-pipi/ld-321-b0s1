package com.example.cyfarmsched.model;

public record TrackPoint(
        String machineCode,
        String taskType,
        String capturedAt,
        double longitude,
        double latitude,
        double speed,
        String fieldBoundary) {
}

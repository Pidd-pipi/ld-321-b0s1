package com.example.cyfarmsched.model;

import java.util.List;

public record DispatchBoard(
        int todayTodos,
        int idleMachines,
        List<String> workingMachines,
        List<String> dueMaintenance,
        List<Integer> sevenDayAreas,
        List<String> trendLabels) {
}

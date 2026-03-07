package com.engineai.crfe.infrastructure.controller;

public record CreateRiskAlertRequest(
        Long userId,
        String alertType,
        String riskLevel,
        String alertMessage,
        Long forecastId
) {
}
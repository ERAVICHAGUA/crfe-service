package com.engineai.crfe.infrastructure.controller;

import java.math.BigDecimal;

public record CreateCashflowForecastRequest(
        Long userId,
        String forecastDate,
        String periodType,
        BigDecimal projectedIncome,
        BigDecimal projectedExpense
) {
}
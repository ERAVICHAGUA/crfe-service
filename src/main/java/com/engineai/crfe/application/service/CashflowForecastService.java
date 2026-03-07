package com.engineai.crfe.application.service;

import com.engineai.crfe.domain.model.CashflowForecast;
import com.engineai.crfe.domain.model.RiskAlert;
import com.engineai.crfe.domain.repository.CashflowForecastRepository;
import com.engineai.crfe.domain.repository.RiskAlertRepository;
import com.engineai.crfe.infrastructure.controller.CreateCashflowForecastRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CashflowForecastService {

    private final CashflowForecastRepository cashflowForecastRepository;
    private final RiskAlertRepository riskAlertRepository;

    public CashflowForecastService(
            CashflowForecastRepository cashflowForecastRepository,
            RiskAlertRepository riskAlertRepository
    ) {
        this.cashflowForecastRepository = cashflowForecastRepository;
        this.riskAlertRepository = riskAlertRepository;
    }

    public CashflowForecast create(CreateCashflowForecastRequest request) {
        BigDecimal income = defaultAmount(request.projectedIncome());
        BigDecimal expense = defaultAmount(request.projectedExpense());
        BigDecimal balance = income.subtract(expense);

        CashflowForecast forecast = CashflowForecast.builder()
                .userId(request.userId())
                .forecastDate(LocalDate.parse(request.forecastDate()))
                .periodType(defaultText(request.periodType(), "monthly"))
                .projectedIncome(income)
                .projectedExpense(expense)
                .projectedBalance(balance)
                .confidenceScore(resolveConfidence(balance))
                .forecastMethod("rules")
                .status("GENERATED")
                .build();

        CashflowForecast savedForecast = cashflowForecastRepository.save(forecast);

        if (balance.compareTo(new BigDecimal("100.00")) < 0) {
            RiskAlert riskAlert = RiskAlert.builder()
                    .userId(savedForecast.getUserId())
                    .alertType(balance.compareTo(BigDecimal.ZERO) < 0 ? "NEGATIVE_BALANCE" : "LOW_LIQUIDITY")
                    .riskLevel(balance.compareTo(BigDecimal.ZERO) < 0 ? "HIGH" : "MEDIUM")
                    .alertMessage(buildAlertMessage(balance))
                    .forecast(savedForecast)
                    .triggeredAt(LocalDateTime.now())
                    .status("ACTIVE")
                    .build();

            riskAlertRepository.save(riskAlert);
        }

        return savedForecast;
    }

    public List<CashflowForecast> findByUserId(Long userId) {
        return cashflowForecastRepository.findByUserIdOrderByForecastDateDesc(userId);
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String defaultText(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private BigDecimal resolveConfidence(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            return new BigDecimal("0.70");
        }
        return new BigDecimal("0.90");
    }

    private String buildAlertMessage(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            return "Tu balance proyectado será negativo. Existe alto riesgo de liquidez.";
        }
        return "Tu balance proyectado es bajo. Existe riesgo de liquidez.";
    }
}
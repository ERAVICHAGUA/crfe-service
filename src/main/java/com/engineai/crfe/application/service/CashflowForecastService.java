package com.engineai.crfe.application.service;

import com.engineai.crfe.domain.model.CashflowForecast;
import com.engineai.crfe.domain.model.RiskAlert;
import com.engineai.crfe.domain.repository.CashflowForecastRepository;
import com.engineai.crfe.domain.repository.RiskAlertRepository;
import com.engineai.crfe.infrastructure.client.TiieClient;
import com.engineai.crfe.infrastructure.client.TransactionResponse;
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
    private final TiieClient tiieClient;

    public CashflowForecastService(
            CashflowForecastRepository cashflowForecastRepository,
            RiskAlertRepository riskAlertRepository,
            TiieClient tiieClient
    ) {
        this.cashflowForecastRepository = cashflowForecastRepository;
        this.riskAlertRepository = riskAlertRepository;
        this.tiieClient = tiieClient;
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
        maybeCreateRiskAlert(savedForecast);

        return savedForecast;
    }

    public CashflowForecast generateFromTransactions(Long userId, String bearerToken) {
        List<TransactionResponse> transactions = tiieClient.getTransactionsByUserId(userId, bearerToken);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> "income".equalsIgnoreCase(t.type()))
                .map(TransactionResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> "expense".equalsIgnoreCase(t.type()))
                .map(TransactionResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        CashflowForecast forecast = CashflowForecast.builder()
                .userId(userId)
                .forecastDate(LocalDate.now().plusDays(30))
                .periodType("monthly")
                .projectedIncome(totalIncome)
                .projectedExpense(totalExpense)
                .projectedBalance(balance)
                .confidenceScore(resolveConfidence(balance))
                .forecastMethod("tiie-aggregation")
                .status("GENERATED")
                .build();

        CashflowForecast savedForecast = cashflowForecastRepository.save(forecast);
        maybeCreateRiskAlert(savedForecast);

        return savedForecast;
    }

    public List<CashflowForecast> findByUserId(Long userId) {
        return cashflowForecastRepository.findByUserIdOrderByForecastDateDesc(userId);
    }

    private void maybeCreateRiskAlert(CashflowForecast forecast) {
        BigDecimal balance = forecast.getProjectedBalance();

        if (balance.compareTo(new BigDecimal("100.00")) < 0) {
            RiskAlert riskAlert = RiskAlert.builder()
                    .userId(forecast.getUserId())
                    .alertType(balance.compareTo(BigDecimal.ZERO) < 0 ? "NEGATIVE_BALANCE" : "LOW_LIQUIDITY")
                    .riskLevel(balance.compareTo(BigDecimal.ZERO) < 0 ? "HIGH" : "MEDIUM")
                    .alertMessage(buildAlertMessage(balance))
                    .forecast(forecast)
                    .triggeredAt(LocalDateTime.now())
                    .status("ACTIVE")
                    .build();

            riskAlertRepository.save(riskAlert);
        }
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
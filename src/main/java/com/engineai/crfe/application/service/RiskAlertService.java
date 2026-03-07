package com.engineai.crfe.application.service;

import com.engineai.crfe.domain.model.CashflowForecast;
import com.engineai.crfe.domain.model.RiskAlert;
import com.engineai.crfe.domain.repository.CashflowForecastRepository;
import com.engineai.crfe.domain.repository.RiskAlertRepository;
import com.engineai.crfe.infrastructure.controller.CreateRiskAlertRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RiskAlertService {

    private final RiskAlertRepository riskAlertRepository;
    private final CashflowForecastRepository cashflowForecastRepository;

    public RiskAlertService(
            RiskAlertRepository riskAlertRepository,
            CashflowForecastRepository cashflowForecastRepository
    ) {
        this.riskAlertRepository = riskAlertRepository;
        this.cashflowForecastRepository = cashflowForecastRepository;
    }

    public RiskAlert create(CreateRiskAlertRequest request) {
        CashflowForecast forecast = null;

        if (request.forecastId() != null) {
            forecast = cashflowForecastRepository.findById(request.forecastId())
                    .orElseThrow(() -> new IllegalArgumentException("Forecast no encontrado"));
        }

        RiskAlert riskAlert = RiskAlert.builder()
                .userId(request.userId())
                .alertType(request.alertType())
                .riskLevel(request.riskLevel())
                .alertMessage(request.alertMessage())
                .forecast(forecast)
                .triggeredAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();

        return riskAlertRepository.save(riskAlert);
    }

    public List<RiskAlert> findByUserId(Long userId) {
        return riskAlertRepository.findByUserIdOrderByTriggeredAtDesc(userId);
    }
}
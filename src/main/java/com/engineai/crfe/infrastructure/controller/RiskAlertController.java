package com.engineai.crfe.infrastructure.controller;

import com.engineai.crfe.application.service.RiskAlertService;
import com.engineai.crfe.domain.model.RiskAlert;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk-alerts")
public class RiskAlertController {

    private final RiskAlertService riskAlertService;

    public RiskAlertController(RiskAlertService riskAlertService) {
        this.riskAlertService = riskAlertService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RiskAlert create(@RequestBody CreateRiskAlertRequest request) {
        return riskAlertService.create(request);
    }

    @GetMapping
    public List<RiskAlert> listByUserId(@RequestParam Long userId) {
        return riskAlertService.findByUserId(userId);
    }
}
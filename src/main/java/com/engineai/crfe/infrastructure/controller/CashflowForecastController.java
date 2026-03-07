package com.engineai.crfe.infrastructure.controller;

import com.engineai.crfe.application.service.CashflowForecastService;
import com.engineai.crfe.domain.model.CashflowForecast;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecasts")
public class CashflowForecastController {

    private final CashflowForecastService cashflowForecastService;

    public CashflowForecastController(CashflowForecastService cashflowForecastService) {
        this.cashflowForecastService = cashflowForecastService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CashflowForecast create(@RequestBody CreateCashflowForecastRequest request) {
        return cashflowForecastService.create(request);
    }

    @GetMapping
    public List<CashflowForecast> listByUserId(@RequestParam Long userId) {
        return cashflowForecastService.findByUserId(userId);
    }
}
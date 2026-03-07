package com.engineai.crfe.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cashflow_forecasts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashflowForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;

    @Column(name = "period_type", nullable = false, length = 20)
    private String periodType;

    @Column(name = "projected_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal projectedIncome;

    @Column(name = "projected_expense", nullable = false, precision = 12, scale = 2)
    private BigDecimal projectedExpense;

    @Column(name = "projected_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal projectedBalance;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "forecast_method", nullable = false, length = 30)
    private String forecastMethod;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
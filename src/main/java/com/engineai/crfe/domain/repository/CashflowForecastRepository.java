package com.engineai.crfe.domain.repository;

import com.engineai.crfe.domain.model.CashflowForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CashflowForecastRepository extends JpaRepository<CashflowForecast, Long> {

    List<CashflowForecast> findByUserIdOrderByForecastDateDesc(Long userId);

    List<CashflowForecast> findByUserIdAndForecastDateGreaterThanEqualOrderByForecastDateAsc(Long userId, LocalDate forecastDate);
}
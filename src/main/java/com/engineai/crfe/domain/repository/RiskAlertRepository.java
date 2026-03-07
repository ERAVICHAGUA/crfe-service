package com.engineai.crfe.domain.repository;

import com.engineai.crfe.domain.model.RiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {

    List<RiskAlert> findByUserIdOrderByTriggeredAtDesc(Long userId);
}
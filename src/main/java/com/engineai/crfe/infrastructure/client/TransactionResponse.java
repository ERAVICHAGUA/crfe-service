package com.engineai.crfe.infrastructure.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long userId,
        String source,
        String type,
        BigDecimal amount,
        String currency,
        String rawDescription,
        String normalizedDescription,
        String merchantRaw,
        String merchantNormalized,
        String category,
        String subcategory,
        BigDecimal confidence,
        String classificationMethod,
        String status,
        LocalDateTime occurredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
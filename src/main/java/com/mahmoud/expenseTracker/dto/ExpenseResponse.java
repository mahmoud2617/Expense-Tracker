package com.mahmoud.expenseTracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mahmoud.expenseTracker.category.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        BigDecimal amount,
        ExpenseCategory category,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate expenseDate,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt
) {
}

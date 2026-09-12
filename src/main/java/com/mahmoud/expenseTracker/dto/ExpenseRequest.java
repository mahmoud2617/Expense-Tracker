package com.mahmoud.expenseTracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mahmoud.expenseTracker.category.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Category is required")
        ExpenseCategory category,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,

        @NotNull(message = "Expense date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate expenseDate
) {
    public ExpenseRequest {
        if (description != null) {
            description = description.trim();
        }
    }
}

package com.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record IncomeDTO(
        String id,
        @NotBlank String title,
        @NotNull @DecimalMin("0.0") BigDecimal amount,
        @NotBlank String category,
        @NotBlank String date,
        String note
) {
}

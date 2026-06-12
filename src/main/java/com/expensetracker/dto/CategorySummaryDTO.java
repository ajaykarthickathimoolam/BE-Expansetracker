package com.expensetracker.dto;

import java.math.BigDecimal;

public record CategorySummaryDTO(String category, BigDecimal amount) {
}

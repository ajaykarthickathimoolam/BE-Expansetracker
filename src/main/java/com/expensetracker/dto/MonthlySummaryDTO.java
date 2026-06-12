package com.expensetracker.dto;

import java.math.BigDecimal;

public record MonthlySummaryDTO(String month, BigDecimal income, BigDecimal expense) {
}

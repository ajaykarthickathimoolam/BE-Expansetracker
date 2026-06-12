package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.MonthlySummaryDTO;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class SummaryService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public SummaryService(ExpenseRepository expenseRepository, IncomeRepository incomeRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    public List<MonthlySummaryDTO> monthlyForUser(String userId) {
        Map<String, BigDecimal> incomeByMonth = new HashMap<>();
        Map<String, BigDecimal> expenseByMonth = new HashMap<>();

        incomeRepository.findByUserIdOrderByDateDesc(userId).forEach(i -> {
            if (i.getDate() != null && i.getDate().length() >= 7) {
                var key = i.getDate().substring(0, 7);
                incomeByMonth.merge(key, i.getAmount(), BigDecimal::add);
            }
        });

        expenseRepository.findByUserIdOrderByDateDesc(userId).forEach(e -> {
            if (e.getDate() != null && e.getDate().length() >= 7) {
                var key = e.getDate().substring(0, 7);
                expenseByMonth.merge(key, e.getAmount(), BigDecimal::add);
            }
        });

        var months = new HashSet<String>();
        months.addAll(incomeByMonth.keySet());
        months.addAll(expenseByMonth.keySet());

        return months.stream()
                .sorted()
                .map(m -> new MonthlySummaryDTO(
                        m,
                        incomeByMonth.getOrDefault(m, BigDecimal.ZERO),
                        expenseByMonth.getOrDefault(m, BigDecimal.ZERO)
                ))
                .toList();
    }

    public List<CategorySummaryDTO> categoryExpensesForUser(String userId) {
        Map<String, BigDecimal> byCat = new HashMap<>();
        expenseRepository.findByUserIdOrderByDateDesc(userId).forEach(e ->
                byCat.merge(e.getCategory(), e.getAmount(), BigDecimal::add)
        );
        return byCat.entrySet().stream()
                .map(e -> new CategorySummaryDTO(e.getKey(), e.getValue()))
                .sorted((a, b) -> b.amount().compareTo(a.amount()))
                .toList();
    }
}

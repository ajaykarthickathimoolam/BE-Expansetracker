package com.expensetracker.controller;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.MonthlySummaryDTO;
import com.expensetracker.service.SummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlySummaryDTO>> monthly() {
        return ResponseEntity.ok(summaryService.monthlyForUser(currentUserId()));
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategorySummaryDTO>> category() {
        return ResponseEntity.ok(summaryService.categoryExpensesForUser(currentUserId()));
    }

    private static String currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "";
    }
}

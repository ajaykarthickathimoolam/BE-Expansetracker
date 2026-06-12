package com.expensetracker.controller;

import com.expensetracker.dto.IncomeDTO;
import com.expensetracker.service.IncomeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping("/incomes")
    public ResponseEntity<List<IncomeDTO>> list() {
        return ResponseEntity.ok(incomeService.listForUser(currentUserId()));
    }

    @PostMapping("/income")
    public ResponseEntity<IncomeDTO> create(@Valid @RequestBody IncomeDTO dto) {
        return ResponseEntity.ok(incomeService.create(currentUserId(), dto));
    }

    @PatchMapping("/income/{id}")
    public ResponseEntity<IncomeDTO> patch(@PathVariable String id, @Valid @RequestBody IncomeDTO dto) {
        return ResponseEntity.ok(incomeService.patch(currentUserId(), id, dto));
    }

    @DeleteMapping("/income/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        incomeService.delete(currentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    private static String currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "";
    }
}

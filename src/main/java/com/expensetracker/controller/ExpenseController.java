package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.service.ExpenseService;
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
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseDTO>> list() {
        return ResponseEntity.ok(expenseService.listForUser(currentUserId()));
    }

    @PostMapping("/expense")
    public ResponseEntity<ExpenseDTO> create(@Valid @RequestBody ExpenseDTO dto) {
        return ResponseEntity.ok(expenseService.create(currentUserId(), dto));
    }

    @PatchMapping("/expense/{id}")
    public ResponseEntity<ExpenseDTO> patch(@PathVariable String id, @Valid @RequestBody ExpenseDTO dto) {
        return ResponseEntity.ok(expenseService.patch(currentUserId(), id, dto));
    }

    @DeleteMapping("/expense/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        expenseService.delete(currentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    private static String currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "";
    }
}

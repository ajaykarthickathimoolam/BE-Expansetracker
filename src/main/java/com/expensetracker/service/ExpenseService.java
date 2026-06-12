package com.expensetracker.service;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.ExpenseCategory;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseDTO> listForUser(String userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream().map(this::toDto).toList();
    }

    public ExpenseDTO create(String userId, ExpenseDTO dto) {
        var normalized = normalizeCategoryInput(dto.category());
        var entity = new Expense();
        entity.setUserId(userId);
        entity.setTitle(dto.title());
        entity.setAmount(dto.amount());
        entity.setCategory(sealedExpenseLabel(normalized));
        entity.setDate(dto.date());
        entity.setNote(dto.note() != null ? dto.note() : "");
        expenseRepository.save(entity);
        return toDto(entity);
    }

    public ExpenseDTO patch(String userId, String id, ExpenseDTO dto) {
        var entity = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        if (!entity.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        entity.setTitle(dto.title());
        entity.setAmount(dto.amount());

        Object categoryInput = dto.category();
        if (categoryInput instanceof String raw) {
            var sealed = normalizeCategoryInput(raw);
            entity.setCategory(switch (sealed) {
                case ExpenseCategory.Food f -> f.value();
                case ExpenseCategory.Transport t -> t.value();
                case ExpenseCategory.Shopping s -> s.value();
                case ExpenseCategory.Health h -> h.value();
                case ExpenseCategory.OtherType o -> o.value();
            });
        }

        entity.setDate(dto.date());
        entity.setNote(dto.note() != null ? dto.note() : "");
        expenseRepository.save(entity);
        return toDto(entity);
    }

    public void delete(String userId, String id) {
        var entity = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        if (!entity.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        expenseRepository.delete(entity);
    }

    private ExpenseCategory normalizeCategoryInput(String raw) {
        return ExpenseCategory.fromValue(raw);
    }

    private String sealedExpenseLabel(ExpenseCategory category) {
        return switch (category) {
            case ExpenseCategory.Food f -> f.value();
            case ExpenseCategory.Transport t -> t.value();
            case ExpenseCategory.Shopping s -> s.value();
            case ExpenseCategory.Health h -> h.value();
            case ExpenseCategory.OtherType o -> o.value();
        };
    }

    private ExpenseDTO toDto(Expense expense) {
        return new ExpenseDTO(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate(),
                expense.getNote()
        );
    }
}

package com.expensetracker.service;

import com.expensetracker.dto.IncomeDTO;
import com.expensetracker.model.Income;
import com.expensetracker.model.IncomeCategory;
import com.expensetracker.repository.IncomeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public List<IncomeDTO> listForUser(String userId) {
        return incomeRepository.findByUserIdOrderByDateDesc(userId).stream().map(this::toDto).toList();
    }

    public IncomeDTO create(String userId, IncomeDTO dto) {
        var sealed = IncomeCategory.fromValue(dto.category());
        var entity = new Income();
        entity.setUserId(userId);
        entity.setTitle(dto.title());
        entity.setAmount(dto.amount());
        entity.setCategory(switch (sealed) {
            case IncomeCategory.Salary s -> s.value();
            case IncomeCategory.Freelance f -> f.value();
            case IncomeCategory.Investment i -> i.value();
            case IncomeCategory.OtherType o -> o.value();
        });
        entity.setDate(dto.date());
        entity.setNote(dto.note() != null ? dto.note() : "");
        incomeRepository.save(entity);
        return toDto(entity);
    }

    public IncomeDTO patch(String userId, String id, IncomeDTO dto) {
        var entity = incomeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income not found"));
        if (!entity.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        entity.setTitle(dto.title());
        entity.setAmount(dto.amount());

        if (dto.category() instanceof String raw) {
            var cat = IncomeCategory.fromValue(raw);
            entity.setCategory(switch (cat) {
                case IncomeCategory.Salary s -> s.value();
                case IncomeCategory.Freelance f -> f.value();
                case IncomeCategory.Investment i -> i.value();
                case IncomeCategory.OtherType o -> o.value();
            });
        }

        entity.setDate(dto.date());
        entity.setNote(dto.note() != null ? dto.note() : "");
        incomeRepository.save(entity);
        return toDto(entity);
    }

    public void delete(String userId, String id) {
        var entity = incomeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income not found"));
        if (!entity.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        incomeRepository.delete(entity);
    }

    private IncomeDTO toDto(Income income) {
        return new IncomeDTO(
                income.getId(),
                income.getTitle(),
                income.getAmount(),
                income.getCategory(),
                income.getDate(),
                income.getNote()
        );
    }
}

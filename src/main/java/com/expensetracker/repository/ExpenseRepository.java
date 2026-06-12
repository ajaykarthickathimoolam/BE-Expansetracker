package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExpenseRepository extends MongoRepository<Expense, String> {

    List<Expense> findByUserIdOrderByDateDesc(String userId);
}

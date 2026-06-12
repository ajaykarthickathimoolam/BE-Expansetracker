package com.expensetracker.repository;

import com.expensetracker.model.Income;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IncomeRepository extends MongoRepository<Income, String> {

    List<Income> findByUserIdOrderByDateDesc(String userId);
}

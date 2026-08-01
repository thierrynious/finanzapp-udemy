package com.finanzmanager.finanzapp.repository;

import com.finanzmanager.finanzapp.model.CategoryBudget;
import com.finanzmanager.finanzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryBudgetRepository
        extends JpaRepository<CategoryBudget, Long> {

    List<CategoryBudget> findByUserAndMonthAndYear(
            User user,
            Integer month,
            Integer year
    );

    Optional<CategoryBudget> findByUserIdAndCategoryIdAndMonthAndYear(
            Long userId,
            Long categoryId,
            Integer month,
            Integer year
    );
}
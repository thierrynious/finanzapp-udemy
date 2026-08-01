package com.finanzmanager.finanzapp.service;

import com.finanzmanager.finanzapp.dto.CategoryBudgetDTO;
import com.finanzmanager.finanzapp.model.Category;
import com.finanzmanager.finanzapp.model.CategoryBudget;
import com.finanzmanager.finanzapp.model.User;
import com.finanzmanager.finanzapp.repository.CategoryBudgetRepository;
import com.finanzmanager.finanzapp.repository.CategoryRepository;
import com.finanzmanager.finanzapp.repository.TransactionRepository;
import com.finanzmanager.finanzapp.service.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CategoryBudgetService {

    private final CategoryBudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public CategoryBudgetService(
            CategoryBudgetRepository budgetRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            CurrentUserService currentUserService
    ) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    public List<CategoryBudgetDTO> getBudgets(Integer month, Integer year) {
        User user = currentUserService.getCurrentUser();

        return budgetRepository.findByUserAndMonthAndYear(user, month, year)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public CategoryBudgetDTO createOrUpdateBudget(CategoryBudgetDTO dto) {
        User user = currentUserService.getCurrentUser();

        Category category = categoryRepository
                .findByIdAndUser(dto.getCategoryId(), user)
                .orElseThrow(() ->
                        new RuntimeException("Kategorie nicht gefunden")
                );

        CategoryBudget budget = budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(
                        user.getId(),
                        dto.getCategoryId(),
                        dto.getMonth(),
                        dto.getYear()
                )
                .orElseGet(CategoryBudget::new);

        budget.setUser(user);
        budget.setCategory(category);
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());
        budget.setAmount(dto.getAmount());

        CategoryBudget savedBudget = budgetRepository.save(budget);

        return toDTO(savedBudget);
    }

    public CategoryBudgetDTO updateBudget(
            Long id,
            CategoryBudgetDTO dto
    ) {
        User user = currentUserService.getCurrentUser();

        CategoryBudget budget = budgetRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Budget nicht gefunden")
                );

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Budget nicht gefunden");
        }

        Category category = categoryRepository
                .findByIdAndUser(dto.getCategoryId(), user)
                .orElseThrow(() ->
                        new RuntimeException("Kategorie nicht gefunden")
                );

        budget.setCategory(category);
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());
        budget.setAmount(dto.getAmount());

        CategoryBudget savedBudget = budgetRepository.save(budget);

        return toDTO(savedBudget);
    }

    public void deleteBudget(Long id) {
        User user = currentUserService.getCurrentUser();

        CategoryBudget budget = budgetRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Budget nicht gefunden")
                );

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Budget nicht gefunden");
        }

        budgetRepository.delete(budget);
    }

    private CategoryBudgetDTO toDTO(CategoryBudget budget) {
        CategoryBudgetDTO dto = new CategoryBudgetDTO();

        dto.setId(budget.getId());
        dto.setCategoryId(budget.getCategory().getId());
        dto.setCategoryName(budget.getCategory().getName());
        dto.setMonth(budget.getMonth());
        dto.setYear(budget.getYear());
        dto.setAmount(budget.getAmount());

        LocalDate startDate = LocalDate.of(
                budget.getYear(),
                budget.getMonth(),
                1
        );

        LocalDate endDate = startDate.withDayOfMonth(
                startDate.lengthOfMonth()
        );

        Double calculatedSpent =
                transactionRepository.calculateSpentAmount(
                        budget.getUser(),
                        budget.getCategory().getId(),
                        startDate,
                        endDate
                );

        double spent = calculatedSpent != null
                ? calculatedSpent
                : 0.0;

        double amount = budget.getAmount() != null
                ? budget.getAmount()
                : 0.0;

        double remaining = amount - spent;

        double percentage = amount > 0
                ? spent / amount * 100
                : 0.0;

        boolean exceeded = spent > amount;

        dto.setSpent(spent);
        dto.setRemaining(remaining);
        dto.setPercentage(percentage);
        dto.setExceeded(exceeded);

        return dto;
    }
}
package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.dto.CategoryBudgetDTO;
import com.finanzmanager.finanzapp.service.CategoryBudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class CategoryBudgetController {

    private final CategoryBudgetService budgetService;

    public CategoryBudgetController(CategoryBudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryBudgetDTO>> getBudgets(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(budgetService.getBudgets(month, year));
    }

    @PostMapping
    public ResponseEntity<CategoryBudgetDTO> createOrUpdateBudget(
            @RequestBody CategoryBudgetDTO dto
    ) {
        return ResponseEntity.ok(budgetService.createOrUpdateBudget(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryBudgetDTO> updateBudget(
            @PathVariable Long id,
            @RequestBody CategoryBudgetDTO dto
    ) {
        return ResponseEntity.ok(budgetService.updateBudget(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.dto.CategoryDTO;
import com.finanzmanager.finanzapp.model.Category;
import com.finanzmanager.finanzapp.model.CategoryType;
import com.finanzmanager.finanzapp.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO dto) {
        Category category = toEntity(dto);
        Category savedCategory = categoryService.createCategory(category);

        return new ResponseEntity<>(toDTO(savedCategory), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> result = categoryService.getAllCategories()
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(categoryService.getCategoryById(id)));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryDTO>> getByType(@PathVariable CategoryType type) {
        List<CategoryDTO> result = categoryService.getByType(type)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoryDTO>> searchByName(@RequestParam String name) {
        List<CategoryDTO> result = categoryService.searchByName(name)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO dto
    ) {
        Category category = toEntity(dto);
        Category updatedCategory = categoryService.updateCategory(id, category);

        return ResponseEntity.ok(toDTO(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setType(category.getType());
        return dto;
    }

    private Category toEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setType(dto.getType());
        return category;
    }
}
package com.finanzmanager.finanzapp.service;

import com.finanzmanager.finanzapp.exception.CategoryNotFoundException;
import com.finanzmanager.finanzapp.model.Category;
import com.finanzmanager.finanzapp.model.CategoryType;
import com.finanzmanager.finanzapp.model.User;
import com.finanzmanager.finanzapp.repository.CategoryRepository;
import com.finanzmanager.finanzapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
    }

    public Category createCategory(Category category) {
        User user = getCurrentUser();

        boolean exists = categoryRepository.existsByUserAndNameIgnoreCaseAndType(
                user,
                category.getName(),
                category.getType()
        );

        if (exists) {
            throw new RuntimeException("Diese Kategorie existiert bereits");
        }

        category.setUser(user);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findByUser(getCurrentUser());
    }

    public Category getCategoryById(Long id) {
        User user = getCurrentUser();

        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public List<Category> getByType(CategoryType type) {
        return categoryRepository.findByUserAndType(getCurrentUser(), type);
    }

    public List<Category> searchByName(String keyword) {
        return categoryRepository.findByUserAndNameContainingIgnoreCase(getCurrentUser(), keyword);
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setType(updatedCategory.getType());

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
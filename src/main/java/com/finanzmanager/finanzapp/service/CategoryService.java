package com.finanzmanager.finanzapp.service;

import com.finanzmanager.finanzapp.model.Category;
import com.finanzmanager.finanzapp.model.CategoryType;
import com.finanzmanager.finanzapp.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    //create
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    //Read alle
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // read nach id
    public Category getCategoryById(Long id) {
        return categoryRepository
                .findById(id).orElseThrow(()->new RuntimeException("Kategorie mit ID"+id+" nicht gefunden"));
    }

    // get by type
    public List<Category> getByType(CategoryType type) {
        return categoryRepository.findByType(type);
    }

    // search by name
    public List<Category> searchByName(String keyword) {
        return categoryRepository.searchByName(keyword);
    }

    //update
    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setType(updatedCategory.getType());

        return categoryRepository.save(existingCategory);
    }

    //delete
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Kategorie mit ID"+id+" nicht gefunden");
        }
        categoryRepository.deleteById(id);
    }
}

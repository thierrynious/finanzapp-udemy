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

    public Category detectCategory(String textForCategory, double amount) {
        User user = getCurrentUser();

        String text = textForCategory == null ? "" : textForCategory.toLowerCase();

        String categoryName;
        CategoryType type = amount >= 0 ? CategoryType.EINNAHME : CategoryType.AUSGABE;

        if (amount >= 0) {
            categoryName = "Einkommen";
        } else if (
                text.contains("rewe")
                        || text.contains("aldi")
                        || text.contains("lidl")
                        || text.contains("edeka")
                        || text.contains("netto")
                        || text.contains("penny")
                        || text.contains("kaufland")
                        || text.contains("supermarkt")
        ) {
            categoryName = "Lebensmittel";
        } else if (
                text.contains("amazon")
                        || text.contains("zalando")
                        || text.contains("ebay")
                        || text.contains("paypal")
                        || text.contains("klarna")
        ) {
            categoryName = "Shopping";
        } else if (
                text.contains("miete")
                        || text.contains("rent")
                        || text.contains("wohnung")
                        || text.contains("vermieter")
        ) {
            categoryName = "Miete";
        } else if (
                text.contains("bahn")
                        || text.contains("deutsche bahn")
                        || text.contains("db ")
                        || text.contains("uber")
                        || text.contains("bolt")
                        || text.contains("tankstelle")
                        || text.contains("shell")
                        || text.contains("aral")
                        || text.contains("esso")
        ) {
            categoryName = "Transport";
        } else if (
                text.contains("vodafone")
                        || text.contains("telekom")
                        || text.contains("o2")
                        || text.contains("telefon")
                        || text.contains("internet")
                        || text.contains("handy")
        ) {
            categoryName = "Telefon & Internet";
        } else if (
                text.contains("strom")
                        || text.contains("gas")
                        || text.contains("energie")
                        || text.contains("wasser")
                        || text.contains("stadtwerke")
        ) {
            categoryName = "Nebenkosten";
        } else if (
                text.contains("versicherung")
                        || text.contains("huk")
                        || text.contains("allianz")
                        || text.contains("axa")
        ) {
            categoryName = "Versicherung";
        } else if (
                text.contains("netflix")
                        || text.contains("spotify")
                        || text.contains("disney")
                        || text.contains("apple")
                        || text.contains("google")
        ) {
            categoryName = "Abos & Unterhaltung";
        } else if (
                text.contains("bargeldauszahlung")
                        || text.contains("geldautomat")
                        || text.contains("atm")
        ) {
            categoryName = "Bargeld";
        } else {
            categoryName = "Sonstiges";
        }

        return categoryRepository
                .findByUserAndNameIgnoreCaseAndType(user, categoryName, type)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(categoryName);
                    category.setType(type);
                    category.setUser(user);
                    return categoryRepository.save(category);
                });
    }
}
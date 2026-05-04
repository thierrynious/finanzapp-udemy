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

    private static final List<CategoryRule> RULES = List.of(
            new CategoryRule("Lebensmittel", CategoryType.AUSGABE, List.of(
                    "rewe", "aldi", "lidl", "edeka", "netto", "penny",
                    "kaufland", "supermarkt", "dm", "rossmann"
            )),

            new CategoryRule("Shopping", CategoryType.AUSGABE, List.of(
                    "amazon", "amzn", "zalando", "ebay", "klarna"
            )),

            new CategoryRule("Miete", CategoryType.AUSGABE, List.of(
                    "miete", "rent", "wohnung", "vermieter"
            )),

            new CategoryRule("Transport", CategoryType.AUSGABE, List.of(
                    "bahn", "deutsche bahn", "db ", "uber", "bolt",
                    "tankstelle", "shell", "aral", "esso"
            )),

            new CategoryRule("Telefon & Internet", CategoryType.AUSGABE, List.of(
                    "vodafone", "telekom", "o2", "telefon", "internet", "handy"
            )),

            new CategoryRule("Nebenkosten", CategoryType.AUSGABE, List.of(
                    "strom", "gas", "energie", "wasser", "stadtwerke"
            )),

            new CategoryRule("Versicherung", CategoryType.AUSGABE, List.of(
                    "versicherung", "huk", "allianz", "axa"
            )),

            new CategoryRule("Abos & Unterhaltung", CategoryType.AUSGABE, List.of(
                    "netflix", "spotify", "disney", "apple", "google",
                    "prime video", "youtube", "deezer"
            )),

            new CategoryRule("Bargeld", CategoryType.AUSGABE, List.of(
                    "bargeldauszahlung", "geldautomat", "atm"
            )),

            new CategoryRule("Einkommen", CategoryType.EINNAHME, List.of(
                    "gehalt", "lohn", "salary", "echtzeit-gutschrift",
                    "gutschrift", "überweisung", "ueberweisung"
            )),
            new CategoryRule("Finanzen", CategoryType.AUSGABE, List.of(
                    "kreditkarte",
                    "kreditkartenabrechnung",
                    "eigene kreditkartenabrechn",
                    "bankgebühr",
                    "entgeltabschluss"
            )),
            new CategoryRule("Alltag", CategoryType.AUSGABE, List.of(
                    "kartenzahlung",
                    "zahlung",
                    "pos"
            )),
            new CategoryRule("Transfers", CategoryType.AUSGABE, List.of(
                    "überweisung",
                    "ueberweisung",
                    "online-ueberweisung",
                    "sepa"
            ))
    );

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
        CategoryType type = amount >= 0 ? CategoryType.EINNAHME : CategoryType.AUSGABE;

        String categoryName = RULES.stream()
                .filter(rule -> rule.matches(text, amount))
                .map(CategoryRule::getCategoryName)
                .findFirst()
                .orElse(type == CategoryType.EINNAHME ? "Einkommen" : "Sonstiges");

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
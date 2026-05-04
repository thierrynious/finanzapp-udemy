package com.finanzmanager.finanzapp.service;

import com.finanzmanager.finanzapp.model.CategoryType;

import java.util.List;

public class CategoryRule {

    private final String categoryName;
    private final CategoryType type;
    private final List<String> keywords;

    public CategoryRule(String categoryName, CategoryType type, List<String> keywords) {
        this.categoryName = categoryName;
        this.type = type;
        this.keywords = keywords;
    }

    public boolean matches(String text, double amount) {
        if (type == CategoryType.EINNAHME && amount < 0) {
            return false;
        }

        if (type == CategoryType.AUSGABE && amount >= 0) {
            return false;
        }

        return keywords.stream()
                .anyMatch(keyword -> text.contains(keyword.toLowerCase()));
    }

    public String getCategoryName() {
        return categoryName;
    }

    public CategoryType getType() {
        return type;
    }
}
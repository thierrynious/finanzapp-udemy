package com.finanzmanager.finanzapp.dto;

import lombok.Data;

@Data
public class CategoryBudgetDTO {

    private Long id;

    private Long categoryId;

    private String categoryName;

    private Integer month;

    private Integer year;

    /**
     * Budget défini par l'utilisateur
     */
    private Double amount;

    /**
     * Dépenses réelles pour ce mois et cette catégorie
     */
    private Double spent;

    /**
     * Budget restant
     */
    private Double remaining;

    /**
     * Pourcentage du budget utilisé (0 - 100+)
     */
    private Double percentage;

    /**
     * Budget dépassé ?
     */
    private Boolean exceeded;
}
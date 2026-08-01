package com.finanzmanager.finanzapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyFinancialReportDTO {

    private Integer month;
    private Integer year;

    private Double totalIncome;
    private Double totalExpenses;
    private Double balance;

    private Double totalFixedExpenses;
    private Double averageExpense;
    private Double biggestExpense;
    private Double biggestIncome;

    private Long transactionCount;
    private Long incomeCount;
    private Long expenseCount;

    @Builder.Default
    private Map<String, Double> expensesByCategory =
            new LinkedHashMap<>();
}
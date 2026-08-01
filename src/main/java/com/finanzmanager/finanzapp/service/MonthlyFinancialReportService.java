package com.finanzmanager.finanzapp.service;

import com.finanzmanager.finanzapp.dto.MonthlyFinancialReportDTO;
import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.model.User;
import com.finanzmanager.finanzapp.repository.TransactionRepository;
import com.finanzmanager.finanzapp.service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlyFinancialReportService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public MonthlyFinancialReportDTO getMonthlyReport(
            Integer month,
            Integer year
    ) {
        validatePeriod(month, year);

        User currentUser = currentUserService.getCurrentUser();

        YearMonth selectedPeriod = YearMonth.of(year, month);
        LocalDate startDate = selectedPeriod.atDay(1);
        LocalDate endDate = selectedPeriod.atEndOfMonth();

        List<Transaction> transactions =
                transactionRepository.findMonthlyTransactions(
                        currentUser,
                        startDate,
                        endDate
                );

        List<Transaction> incomeTransactions = transactions.stream()
                .filter(Transaction::isIncome)
                .toList();

        List<Transaction> expenseTransactions = transactions.stream()
                .filter(transaction -> !transaction.isIncome())
                .toList();

        double totalIncome = incomeTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = expenseTransactions.stream()
                .mapToDouble(transaction ->
                        Math.abs(transaction.getAmount())
                )
                .sum();

        double biggestIncome = incomeTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .max()
                .orElse(0.0);

        double biggestExpense = expenseTransactions.stream()
                .mapToDouble(transaction ->
                        Math.abs(transaction.getAmount())
                )
                .max()
                .orElse(0.0);

        double averageExpense = expenseTransactions.stream()
                .mapToDouble(transaction ->
                        Math.abs(transaction.getAmount())
                )
                .average()
                .orElse(0.0);

        Map<String, Double> groupedExpenses =
                expenseTransactions.stream()
                        .collect(Collectors.groupingBy(
                                this::resolveCategoryName,
                                Collectors.summingDouble(
                                        transaction ->
                                                Math.abs(
                                                        transaction.getAmount()
                                                )
                                )
                        ));

        Map<String, Double> expensesByCategory =
                groupedExpenses.entrySet()
                        .stream()
                        .sorted(
                                Map.Entry
                                        .<String, Double>comparingByValue()
                                        .reversed()
                        )
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (existing, replacement) -> existing,
                                LinkedHashMap::new
                        ));

        double totalFixedExpenses = expenseTransactions.stream()
                .filter(this::isFixedExpense)
                .mapToDouble(transaction ->
                        Math.abs(transaction.getAmount())
                )
                .sum();

        return MonthlyFinancialReportDTO.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(totalIncome - totalExpenses)
                .totalFixedExpenses(totalFixedExpenses)
                .averageExpense(averageExpense)
                .biggestExpense(biggestExpense)
                .biggestIncome(biggestIncome)
                .transactionCount((long) transactions.size())
                .incomeCount((long) incomeTransactions.size())
                .expenseCount((long) expenseTransactions.size())
                .expensesByCategory(expensesByCategory)
                .build();
    }

    private String resolveCategoryName(Transaction transaction) {
        if (transaction.getCategory() == null) {
            return "Sonstiges";
        }

        return transaction.getCategory().getName();
    }

    private boolean isFixedExpense(Transaction transaction) {
        if (transaction.getCategory() == null) {
            return false;
        }

        String categoryName = transaction
                .getCategory()
                .getName()
                .toLowerCase(Locale.ROOT);

        return categoryName.contains("miete")
                || categoryName.contains("versicherung")
                || categoryName.contains("abo")
                || categoryName.contains("schulden")
                || categoryName.contains("finanzen")
                || categoryName.contains("telefon")
                || categoryName.contains("internet");
    }

    private void validatePeriod(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Der Monat muss zwischen 1 und 12 liegen."
            );
        }

        if (year == null || year < 2000 || year > 2100) {
            throw new IllegalArgumentException(
                    "Das Jahr muss zwischen 2000 und 2100 liegen."
            );
        }
    }
}
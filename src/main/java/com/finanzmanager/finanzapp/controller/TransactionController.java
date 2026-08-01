package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.dto.TransactionDTO;
import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getTransactions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean income,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("date"),
                        Sort.Order.desc("id")
                )
        );

        Page<TransactionDTO> result = service
                .getFilteredPaged(search, income, categoryId, pageable)
                .map(this::toDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @Valid @RequestBody TransactionDTO dto
    ) {
        Transaction entity = toEntity(dto);

        Transaction saved = service.save(
                entity,
                dto.getCategoryId()
        );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/transactions/" + saved.getId()
                        )
                )
                .body(toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO dto
    ) {
        Transaction entity = toEntity(dto);

        Transaction updated = service.update(
                id,
                entity,
                dto.getCategoryId()
        );

        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id
    ) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        List<Transaction> transactions = service.getAll();

        if (year != null) {
            transactions = transactions.stream()
                    .filter(tx -> tx.getDate() != null)
                    .filter(tx -> tx.getDate().getYear() == year)
                    .toList();
        }

        if (month != null) {
            transactions = transactions.stream()
                    .filter(tx -> tx.getDate() != null)
                    .filter(tx ->
                            tx.getDate().getMonthValue() == month
                    )
                    .toList();
        }

        double totalIncome = transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(tx -> !tx.isIncome())
                .mapToDouble(tx -> Math.abs(tx.getAmount()))
                .sum();

        double balance = totalIncome - totalExpenses;

        long transactionCount = transactions.size();

        double biggestExpense = transactions.stream()
                .filter(tx -> !tx.isIncome())
                .mapToDouble(tx -> Math.abs(tx.getAmount()))
                .max()
                .orElse(0.0);

        double biggestIncome = transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .max()
                .orElse(0.0);

        Map<String, Double> categoryStats = transactions.stream()
                .filter(tx -> !tx.isIncome())
                .filter(tx -> tx.getCategory() != null)
                .collect(
                        Collectors.groupingBy(
                                tx -> tx.getCategory().getName(),
                                Collectors.summingDouble(
                                        tx -> Math.abs(tx.getAmount())
                                )
                        )
                );

        List<TransactionDTO> latestTransactions =
                transactions.stream()
                        .filter(tx -> tx.getDate() != null)
                        .sorted((a, b) -> {
                            int cmp =
                                    b.getDate().compareTo(a.getDate());

                            if (cmp == 0) {
                                return Long.compare(
                                        b.getId(),
                                        a.getId()
                                );
                            }

                            return cmp;
                        })
                        .limit(5)
                        .map(this::toDTO)
                        .toList();

        Map<String, Object> dashboard =
                new LinkedHashMap<>();

        dashboard.put("balance", balance);
        dashboard.put("totalIncome", totalIncome);
        dashboard.put("totalExpenses", totalExpenses);
        dashboard.put("transactionCount", transactionCount);
        dashboard.put("biggestExpense", biggestExpense);
        dashboard.put("biggestIncome", biggestIncome);
        dashboard.put("categoryStats", categoryStats);
        dashboard.put(
                "latestTransactions",
                latestTransactions
        );

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{id}")
    public TransactionDTO getById(
            @PathVariable long id
    ) {
        return toDTO(service.getById(id));
    }

    private TransactionDTO toDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();

        dto.setId(tx.getId());
        dto.setTitle(tx.getTitle());
        dto.setAmount(Math.abs(tx.getAmount()));
        dto.setDate(tx.getDate());
        dto.setIncome(tx.isIncome());

        if (tx.getCategory() != null) {
            dto.setCategoryId(
                    tx.getCategory().getId()
            );

            dto.setCategory(
                    tx.getCategory().getName()
            );
        } else {
            dto.setCategoryId(null);
            dto.setCategory("Unbekannt");
        }

        return dto;
    }

    private Transaction toEntity(TransactionDTO dto) {
        double signedAmount = dto.isIncome()
                ? Math.abs(dto.getAmount())
                : -Math.abs(dto.getAmount());

        return new Transaction(
                dto.getTitle(),
                signedAmount,
                dto.getDate()
        );
    }
}
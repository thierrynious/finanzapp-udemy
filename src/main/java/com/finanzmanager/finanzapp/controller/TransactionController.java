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
                Sort.by(Sort.Order.desc("date"), Sort.Order.desc("id"))
        );

        Page<TransactionDTO> result = service
                .getFilteredPaged(search, income, categoryId, pageable)
                .map(this::toDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO dto) {
        Transaction entity = toEntity(dto);
        Transaction saved = service.save(entity);

        return ResponseEntity
                .created(URI.create("/api/transactions/" + saved.getId()))
                .body(toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO dto
    ) {
        Transaction entity = toEntity(dto);
        Transaction updated = service.update(id, entity, dto.getCategoryId());
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        List<Transaction> transactions = service.getAll();

        double totalIncome = transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(tx -> !tx.isIncome())
                .mapToDouble(tx -> Math.abs(tx.getAmount()))
                .sum();

        double balance = totalIncome - totalExpenses;

        List<TransactionDTO> latestTransactions = transactions.stream()
                .sorted((a, b) -> {
                    int cmp = b.getDate().compareTo(a.getDate());
                    if (cmp == 0) {
                        return Long.compare(b.getId(), a.getId());
                    }
                    return cmp;
                })
                .limit(5)
                .map(this::toDTO)
                .toList();

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("balance", balance);
        dashboard.put("totalIncome", totalIncome);
        dashboard.put("totalExpenses", totalExpenses);
        dashboard.put("latestTransactions", latestTransactions);

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{id}")
    public TransactionDTO getById(@PathVariable long id) {
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
            dto.setCategoryId(tx.getCategory().getId());
            dto.setCategory(tx.getCategory().getName());
        } else {
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
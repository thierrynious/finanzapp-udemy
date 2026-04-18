package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.dto.TransactionDTO;
import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> result = service.getAll()
                .stream()
                .map(this::toDTO)
                .toList();
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
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
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

    @GetMapping("/search")
    public ResponseEntity<List<TransactionDTO>> searchByTitle(@RequestParam String title) {
        List<TransactionDTO> result = service.searchByTitle(title)
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<TransactionDTO>> getPagedTransactions(Pageable pageable) {
        Page<TransactionDTO> result = service.getPaged(pageable)
                .map(this::toDTO);
        return ResponseEntity.ok(result);
    }

    private TransactionDTO toDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setTitle(tx.getTitle());
        dto.setAmount(tx.getAmount());
        dto.setDate(tx.getDate());
        dto.setIncome(tx.isIncome());
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
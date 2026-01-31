package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = service.getAll();
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody Transaction transaction) {
        if(transaction.getId() != null) {
            return ResponseEntity.badRequest().body("Id muss null sein - Wird automatisch generiert");
        }
        Transaction saved = service.save(transaction);
        return ResponseEntity.created(URI.create("/api/transactions/"+saved.getId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable long id) {
        Transaction found = service.getById(id);
        if (found != null) {
            return ResponseEntity.ok(found); // 200 ok
        }else {
            return ResponseEntity.notFound().build(); //404
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Transaction>> searchByTitle(@RequestParam String title) {
        List<Transaction> results = service.searchByTitle(title);
        return ResponseEntity.ok(results);
    }
}

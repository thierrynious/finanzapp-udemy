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
import java.util.List;

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

        return ResponseEntity.created(URI.create("/api/transactions/" + saved.getId())).body(toDTO(saved));
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

    //Entity - DTO
    private TransactionDTO toDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setTitle(tx.getTitle());
        dto.setAmount(tx.getAmount());
        dto.setDate(tx.getDate());
        return dto;
    }

    //DTO - Entity
    private Transaction toEntity(TransactionDTO dto) {
        return new Transaction(
                dto.getTitle(),
                dto.getAmount(),
                dto.getDate()
        );
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<TransactionDTO>> getPagedTransactions(Pageable pageable) {
        Page<TransactionDTO> result = service.getPaged(pageable)
                .map(this::toDTO);
        return ResponseEntity.ok(result);
    }
}

package com.finanzmanager.finanzapp.service;

import com.finanzmanager.finanzapp.config.AppProperties;
import com.finanzmanager.finanzapp.exception.TransactionNotFoundException;
import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository repository;
    private final AppProperties appProperties;

    public TransactionService(TransactionRepository repository, AppProperties appProperties) {
        this.repository = repository;
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void init() {
        log.info("{} gestartet | Max. Transaktionen: {} | Währung: {}",
                appProperties.getName(),
                appProperties.getMaxTransactions(),
                appProperties.getDefaultCurrency());
    }

    public List<Transaction> getAll() {
        return repository.findAll();
    }

    public Page<Transaction> getFilteredPaged(String search, Boolean income, Pageable pageable) {
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
        return repository.findFiltered(normalizedSearch, income, pageable);
    }

    public Transaction save(Transaction transaction) {
        return repository.save(transaction);
    }

    public Transaction getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public void deleteById(Long id) {
        Transaction tx = getById(id);
        repository.delete(tx);
    }
}
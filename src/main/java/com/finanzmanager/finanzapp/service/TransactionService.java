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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository repository;
    private final AppProperties appProperties; //Konfiguration injiziert

    public TransactionService(TransactionRepository repository, AppProperties appProperties) {
        this.repository = repository;
        this.appProperties = appProperties;
    }

    // Wird beim Start der App automatisch ausgeführt
    @PostConstruct
    public void init() {
        log.info("{} gestartet | Max. Transaktionen: {} | Währung: {}",
        appProperties.getName(),
                appProperties.getMaxTransactions(),
                appProperties.getDefaultCurrency());
    }

    // Alle Transaktionen ohne Paging
    public List<Transaction> getAll() {
        return repository.findAll();
    }

    // Neu: Pagination und Sorting
    public Page<Transaction> getPaged(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Transaction save(Transaction transaction) {
            return repository.save(transaction);
    }

    public Transaction getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public List<Transaction> searchByTitle(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }
}

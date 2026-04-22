package com.finanzmanager.finanzapp.service;

import com.finanzmanager.finanzapp.config.AppProperties;
import com.finanzmanager.finanzapp.exception.TransactionNotFoundException;
import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.model.User;
import com.finanzmanager.finanzapp.repository.TransactionRepository;
import com.finanzmanager.finanzapp.service.security.CurrentUserService;
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
    private final CurrentUserService currentUserService;

    public TransactionService(
            TransactionRepository repository,
            AppProperties appProperties,
            CurrentUserService currentUserService
    ) {
        this.repository = repository;
        this.appProperties = appProperties;
        this.currentUserService = currentUserService;
    }

    @PostConstruct
    public void init() {
        log.info("{} gestartet | Max. Transaktionen: {} | Währung: {}",
                appProperties.getName(),
                appProperties.getMaxTransactions(),
                appProperties.getDefaultCurrency());
    }

    public List<Transaction> getAll() {
        User currentUser = currentUserService.getCurrentUser();
        return repository.findFilteredByUser(currentUser, null, null, Pageable.unpaged()).getContent();
    }

    public Page<Transaction> getFilteredPaged(String search, Boolean income, Pageable pageable) {
        User currentUser = currentUserService.getCurrentUser();
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
        return repository.findFilteredByUser(currentUser, normalizedSearch, income, pageable);
    }

    public Transaction save(Transaction transaction) {
        User currentUser = currentUserService.getCurrentUser();
        transaction.setUser(currentUser);
        return repository.save(transaction);
    }

    public Transaction getById(Long id) {
        User currentUser = currentUserService.getCurrentUser();

        Transaction tx = repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        if (!tx.getUser().getId().equals(currentUser.getId())) {
            throw new TransactionNotFoundException(id);
        }

        return tx;
    }

    public void deleteById(Long id) {
        Transaction tx = getById(id);
        repository.delete(tx);
    }
}
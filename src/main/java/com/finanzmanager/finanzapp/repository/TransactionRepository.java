package com.finanzmanager.finanzapp.repository;

import com.finanzmanager.finanzapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);
    List<Transaction> findByTitleContainingIgnoreCase(String title);
}

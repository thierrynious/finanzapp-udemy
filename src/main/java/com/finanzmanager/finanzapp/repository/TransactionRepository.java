package com.finanzmanager.finanzapp.repository;

import com.finanzmanager.finanzapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTitleContainingIgnoreCase(String title);
}

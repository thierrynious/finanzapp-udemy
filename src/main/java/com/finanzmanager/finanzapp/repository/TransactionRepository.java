package com.finanzmanager.finanzapp.repository;

import com.finanzmanager.finanzapp.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (
                :income IS NULL
                OR (:income = true AND t.amount >= 0)
                OR (:income = false AND t.amount < 0)
              )
    """)
    Page<Transaction> findFiltered(String search, Boolean income, Pageable pageable);

    List<Transaction> findByTitleContainingIgnoreCase(String gehalt);
}
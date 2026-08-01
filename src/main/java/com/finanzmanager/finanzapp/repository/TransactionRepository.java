package com.finanzmanager.finanzapp.repository;

import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t
        FROM Transaction t
        LEFT JOIN FETCH t.category
        WHERE t.user = :user
          AND (:search IS NULL
               OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (
                :income IS NULL
                OR (:income = true AND t.amount >= 0)
                OR (:income = false AND t.amount < 0)
              )
          AND (:categoryId IS NULL OR t.category.id = :categoryId)
    """)
    Page<Transaction> findFilteredByUser(
            User user,
            String search,
            Boolean income,
            Long categoryId,
            Pageable pageable
    );

    Optional<Transaction> findByIdAndUser(
            Long id,
            User user
    );

    @Query("""
        SELECT t
        FROM Transaction t
        LEFT JOIN FETCH t.category
        WHERE t.user = :user
          AND t.date BETWEEN :startDate AND :endDate
        ORDER BY t.date DESC, t.id DESC
    """)
    List<Transaction> findMonthlyTransactions(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Transaction> findByUserAndTitleContainingIgnoreCase(
            User user,
            String title
    );

    @Query("""
        SELECT COALESCE(SUM(ABS(t.amount)), 0)
        FROM Transaction t
        WHERE t.user = :user
          AND t.category.id = :categoryId
          AND t.amount < 0
          AND t.date BETWEEN :startDate AND :endDate
    """)
    Double calculateSpentAmount(
            User user,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate
    );
}
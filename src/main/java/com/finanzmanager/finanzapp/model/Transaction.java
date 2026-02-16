package com.finanzmanager.finanzapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transaction")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    public Transaction() {
    }

    public Transaction(String title, double amount, LocalDate date) {
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public Transaction(Long id, String title, double amount, LocalDate date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel darf nicht leer sein")
    private String title;

    @Positive(message = "Betrag muss positiv sein")
    private double amount;

    @NotNull(message = "Datum darf nicht null sein")
    @PastOrPresent(message = "Datum darf nicht in der Zukunft liegen")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @CreatedDate
    @Column(nullable=false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;



}

package com.finanzmanager.finanzapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="bank_account")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance;

    public void applyTransaction(double amount) {
        this.balance += amount;
    }
}

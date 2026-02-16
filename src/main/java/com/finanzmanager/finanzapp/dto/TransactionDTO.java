package com.finanzmanager.finanzapp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionDTO {

    private Long id;
    private String title;
    private double amount;
    private LocalDate date;

}

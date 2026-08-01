package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        TransactionController transactionController =
                new TransactionController(transactionService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(transactionController)
                .build();
    }
    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {

        // given
        Transaction savedTransaction = new Transaction(
                "Miete",
                1200.0,
                LocalDate.of(2023, 10, 1)
        );
        savedTransaction.setId(1L);

        when(transactionService.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        // when / then
        mockMvc.perform(
                        post("/api/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "title": "Miete",
                                      "amount": 1200.0,
                                      "date": "2023-10-01"
                                    }
                                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Miete"))
                .andExpect(jsonPath("$.amount").value(1200.0))
                .andExpect(jsonPath("$.date").value("2023-10-01"));

        verify(transactionService)
                .save(any(Transaction.class));
    }
}
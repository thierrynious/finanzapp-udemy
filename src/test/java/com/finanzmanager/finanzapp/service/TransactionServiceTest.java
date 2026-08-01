package com.finanzmanager.finanzapp.service;

import com.finanzmanager.finanzapp.config.AppProperties;
import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.model.User;
import com.finanzmanager.finanzapp.repository.CategoryRepository;
import com.finanzmanager.finanzapp.repository.TransactionRepository;
import com.finanzmanager.finanzapp.service.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AppProperties appProperties;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldReturnAllTransactionsForCurrentUser() {

        // given
        User currentUser = createUser();

        Transaction rent = new Transaction(
                "Miete",
                -1200.0,
                LocalDate.now()
        );
        rent.setUser(currentUser);

        Transaction salary = new Transaction(
                "Gehalt",
                2500.0,
                LocalDate.now()
        );
        salary.setUser(currentUser);

        List<Transaction> transactions = List.of(
                rent,
                salary
        );

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(transactionRepository.findFilteredByUser(
                currentUser,
                null,
                null,
                null,
                Pageable.unpaged()
        )).thenReturn(new PageImpl<>(transactions));

        // when
        List<Transaction> result = transactionService.getAll();

        // then
        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(Transaction::getTitle)
                .containsExactly("Miete", "Gehalt");

        verify(currentUserService).getCurrentUser();

        verify(transactionRepository).findFilteredByUser(
                currentUser,
                null,
                null,
                null,
                Pageable.unpaged()
        );
    }

    @Test
    void shouldSaveTransactionForCurrentUser() {

        // given
        User currentUser = createUser();

        Transaction transaction = new Transaction(
                "Test",
                -99.99,
                LocalDate.now()
        );

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Transaction result = transactionService.save(transaction);

        // then
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction savedTransaction = captor.getValue();

        assertThat(savedTransaction.getTitle())
                .isEqualTo("Test");

        assertThat(savedTransaction.getAmount())
                .isEqualTo(-99.99);

        assertThat(savedTransaction.getDate())
                .isEqualTo(LocalDate.now());

        assertThat(savedTransaction.getUser())
                .isSameAs(currentUser);

        assertThat(savedTransaction.isIncome())
                .isFalse();

        assertThat(result)
                .isSameAs(savedTransaction);

        verify(currentUserService).getCurrentUser();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@test.de")
                .password("Password123!")
                .isActive(true)
                .build();
    }
}
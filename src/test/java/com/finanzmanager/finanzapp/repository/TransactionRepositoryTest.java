package com.finanzmanager.finanzapp.repository;

import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Speichern und Laden einer Transaktion")
    void shouldSaveAndLoadTransaction() {

        // given
        User user = createAndSaveUser(
                "testuser1",
                "testuser1@test.de"
        );

        Transaction transaction = new Transaction(
                "Miete",
                -1000.0,
                LocalDate.of(2025, 1, 1)
        );
        transaction.setUser(user);

        // when
        transactionRepository.save(transaction);

        List<Transaction> result =
                transactionRepository.findAll();

        // then
        assertThat(result).hasSize(1);

        Transaction savedTransaction = result.getFirst();

        assertThat(savedTransaction.getId()).isNotNull();
        assertThat(savedTransaction.getTitle()).isEqualTo("Miete");
        assertThat(savedTransaction.getAmount()).isEqualTo(-1000.0);
        assertThat(savedTransaction.getDate())
                .isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(savedTransaction.getUser().getId())
                .isEqualTo(user.getId());
        assertThat(savedTransaction.isIncome()).isFalse();
    }

    @Test
    @DisplayName("Suche nach Titel ohne Beachtung der Groß- und Kleinschreibung")
    void shouldFindByUserAndTitleIgnoreCase() {

        // given
        User user = createAndSaveUser(
                "testuser2",
                "testuser2@test.de"
        );

        Transaction salary = new Transaction(
                "Gehalt Januar",
                3000.0,
                LocalDate.now()
        );
        salary.setUser(user);

        Transaction rent = new Transaction(
                "Miete Januar",
                -1000.0,
                LocalDate.now()
        );
        rent.setUser(user);

        transactionRepository.saveAll(
                List.of(salary, rent)
        );

        // when
        List<Transaction> result =
                transactionRepository
                        .findByUserAndTitleContainingIgnoreCase(
                                user,
                                "GEHALT"
                        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle())
                .isEqualTo("Gehalt Januar");
        assertThat(result.getFirst().getUser().getId())
                .isEqualTo(user.getId());
        assertThat(result.getFirst().isIncome()).isTrue();
    }

    @Test
    @DisplayName("Ein Benutzer darf nur seine eigenen Transaktionen finden")
    void shouldReturnOnlyTransactionsOfSelectedUser() {

        // given
        User firstUser = createAndSaveUser(
                "firstuser",
                "firstuser@test.de"
        );

        User secondUser = createAndSaveUser(
                "seconduser",
                "seconduser@test.de"
        );

        Transaction firstTransaction = new Transaction(
                "Gehalt Juli",
                3000.0,
                LocalDate.now()
        );
        firstTransaction.setUser(firstUser);

        Transaction secondTransaction = new Transaction(
                "Gehalt Juli",
                4500.0,
                LocalDate.now()
        );
        secondTransaction.setUser(secondUser);

        transactionRepository.saveAll(
                List.of(firstTransaction, secondTransaction)
        );

        // when
        List<Transaction> result =
                transactionRepository
                        .findByUserAndTitleContainingIgnoreCase(
                                firstUser,
                                "Gehalt"
                        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAmount())
                .isEqualTo(3000.0);
        assertThat(result.getFirst().getUser().getId())
                .isEqualTo(firstUser.getId());
    }

    private User createAndSaveUser(
            String username,
            String email
    ) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password("Password123!")
                .isActive(true)
                .build();

        return userRepository.save(user);
    }
}
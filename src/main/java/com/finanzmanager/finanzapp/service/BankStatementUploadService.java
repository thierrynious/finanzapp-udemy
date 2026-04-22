package com.finanzmanager.finanzapp.service;

import com.finanzmanager.finanzapp.dto.BankStatementImportResult;
import com.finanzmanager.finanzapp.model.Transaction;
import com.finanzmanager.finanzapp.model.User;
import com.finanzmanager.finanzapp.repository.TransactionRepository;
import com.finanzmanager.finanzapp.service.security.CurrentUserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BankStatementUploadService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public BankStatementUploadService(TransactionRepository transactionRepository, CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public BankStatementImportResult importCsv(MultipartFile file) {
        User currentUser = currentUserService.getCurrentUser();
        int totalParsed = 0;
        int imported = 0;
        int duplicates = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                return new BankStatementImportResult(0, 0, 0);
            }

            headerLine = headerLine.replace("\"", "");
            String[] headers = headerLine.split(";");

            int dateIndex = findIndex(headers, "Buchungstag");
            int textIndex = findIndex(headers, "Buchungstext");
            int purposeIndex = findIndex(headers, "Verwendungszweck");
            int amountIndex = findIndex(headers, "Betrag");

            if (dateIndex == -1 || amountIndex == -1) {
                throw new IllegalArgumentException("Pflichtspalten nicht gefunden.");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                line = line.replace("\"", "");
                String[] parts = line.split(";", -1);

                if (parts.length <= amountIndex || parts.length <= dateIndex) {
                    continue;
                }

                String dateRaw = parts[dateIndex].trim();
                String bookingText = textIndex >= 0 && textIndex < parts.length ? parts[textIndex].trim() : "";
                String purpose = purposeIndex >= 0 && purposeIndex < parts.length ? parts[purposeIndex].trim() : "";
                String amountRaw = parts[amountIndex].trim();

                if (dateRaw.isBlank() || amountRaw.isBlank()) {
                    continue;
                }

                totalParsed++;

                String normalizedAmount = amountRaw
                        .replace(".", "")
                        .replace(",", ".");

                double amount = Double.parseDouble(normalizedAmount);
                LocalDate parsedDate = parseDate(dateRaw);

                String resolvedTitle = !bookingText.isBlank() ? bookingText : purpose;
                if (resolvedTitle.isBlank()) {
                    resolvedTitle = "Importierte Transaktion";
                }

                final String title = resolvedTitle;
                final double finalAmount = amount;
                final LocalDate finalParsedDate = parsedDate;

                // Einfache Duplikatsprüfung:
                boolean exists = transactionRepository
                        .findFilteredByUser(currentUser,
                                null,
                                null,
                                org.springframework.data.domain.Pageable.unpaged())
                        .getContent()
                        .stream()
                        .anyMatch(tx ->
                                tx.getDate().equals(finalParsedDate)
                                        && tx.getTitle().equalsIgnoreCase(title)
                                        && Double.compare(tx.getAmount(), finalAmount) == 0
                        );

                if (exists) {
                    duplicates++;
                    continue;
                }

                Transaction transaction = new Transaction();
                transaction.setTitle(title);
                transaction.setAmount(finalAmount);
                transaction.setDate(finalParsedDate);
                transaction.setUser(currentUser);

                transactionRepository.save(transaction);
                imported++;
            }

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Verarbeiten der CSV: " + e.getMessage(), e);
        }

        return new BankStatementImportResult(totalParsed, imported, duplicates);
    }

    private int findIndex(String[] headers, String target) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(target)) {
                return i;
            }
        }
        return -1;
    }

    private LocalDate parseDate(String raw) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd.MM.yy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(raw, formatter);
            } catch (Exception ignored) {
            }
        }

        throw new IllegalArgumentException("Ungültiges Datum: " + raw);
    }
}
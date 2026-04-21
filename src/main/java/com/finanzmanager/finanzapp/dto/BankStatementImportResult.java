package com.finanzmanager.finanzapp.dto;

public class BankStatementImportResult {
    private int totalParsed;
    private int imported;
    private int duplicates;

    public BankStatementImportResult(int totalParsed, int imported, int duplicates) {
        this.totalParsed = totalParsed;
        this.imported = imported;
        this.duplicates = duplicates;
    }

    public int getTotalParsed() {
        return totalParsed;
    }

    public int getImported() {
        return imported;
    }

    public int getDuplicates() {
        return duplicates;
    }
}
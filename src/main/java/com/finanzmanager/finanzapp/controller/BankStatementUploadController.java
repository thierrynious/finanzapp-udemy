package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.dto.BankStatementImportResult;
import com.finanzmanager.finanzapp.service.BankStatementUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class BankStatementUploadController {

    private final BankStatementUploadService uploadService;

    public BankStatementUploadController(BankStatementUploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/bankstatement")
    public ResponseEntity<Map<String, Object>> uploadBankStatement(
            @RequestParam("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Datei ist leer"
            ));
        }

        BankStatementImportResult result = uploadService.importCsv(file);

        return ResponseEntity.ok(Map.of(
                "totalParsed", result.getTotalParsed(),
                "imported", result.getImported(),
                "duplicates", result.getDuplicates(),
                "fileName", file.getOriginalFilename()
        ));
    }
}
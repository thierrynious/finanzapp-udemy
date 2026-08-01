package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.dto.MonthlyFinancialReportDTO;
import com.finanzmanager.finanzapp.service.MonthlyFinancialReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class MonthlyFinancialReportController {

    private final MonthlyFinancialReportService reportService;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyFinancialReportDTO> getMonthlyReport(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(
                reportService.getMonthlyReport(month, year)
        );
    }
}
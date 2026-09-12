package com.mahmoud.expenseTracker.controller;

import com.mahmoud.expenseTracker.dto.ExpenseRequest;
import com.mahmoud.expenseTracker.dto.ExpenseResponse;
import com.mahmoud.expenseTracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        return new ResponseEntity<>(expenseService.createExpense(request), HttpStatus.CREATED);
    }

    @GetMapping("/expenses")
    public ResponseEntity<Page<ExpenseResponse>> getExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "newest") String sort
    ) {
        return ResponseEntity.ok(expenseService.getExpenses(page, size, search, category, from == null ? null : from.toString(), to == null ? null : to.toString(), sort));
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<ExpenseResponse> getExpense(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpense(id));
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(id, request));
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expenses/statistics/total")
    public ResponseEntity<BigDecimal> getTotalSpending(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(expenseService.getTotalSpending(category, from == null ? null : from.toString(), to == null ? null : to.toString()));
    }

    @GetMapping("/expenses/statistics/by-category")
    public ResponseEntity<Map<String, BigDecimal>> getSpendingByCategory(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(expenseService.getSpendingByCategory(category, from == null ? null : from.toString(), to == null ? null : to.toString()));
    }

    @GetMapping("/expenses/statistics/most-expensive")
    public ResponseEntity<ExpenseResponse> getMostExpensive(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        ExpenseResponse response = expenseService.getMostExpensive(category, from == null ? null : from.toString(), to == null ? null : to.toString());
        return response == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }
}

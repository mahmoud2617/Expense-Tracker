package com.mahmoud.expenseTracker.service;

import com.mahmoud.expenseTracker.dto.ExpenseRequest;
import com.mahmoud.expenseTracker.dto.ExpenseResponse;
import com.mahmoud.expenseTracker.entity.Expense;
import com.mahmoud.expenseTracker.category.ExpenseCategory;
import com.mahmoud.expenseTracker.exception.ExpenseNotFoundException;
import com.mahmoud.expenseTracker.repository.ExpenseRepository;
import com.mahmoud.expenseTracker.specification.ExpenseSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Expense expense = Expense.builder()
                .amount(request.amount())
                .category(request.category())
                .description(request.description())
                .expenseDate(request.expenseDate())
                .build();

        return toResponse(expenseRepository.save(expense));
    }

    public Page<ExpenseResponse> getExpenses(int page, int size, String search, String category, String from, String to, String sort) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);

        Specification<Expense> specification = ExpenseSpecifications.withFilters(
                search,
                parseCategory(category),
                parseDate(from),
                parseDate(to)
        );

        Pageable pageable = PageRequest.of(safePage, safeSize, resolveSort(sort));
        return expenseRepository.findAll(specification, pageable).map(this::toResponse);
    }

    public ExpenseResponse getExpense(Long id) {
        return toResponse(findExpense(id));
    }

    @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Expense expense = findExpense(id);
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setDescription(request.description());
        expense.setExpenseDate(request.expenseDate());
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public void deleteExpense(Long id) {
        expenseRepository.delete(findExpense(id));
    }

    public BigDecimal getTotalSpending(String category, String from, String to) {
        List<Expense> expenses = expenseRepository.findAll(
                ExpenseSpecifications.withFilters(null, parseCategory(category), parseDate(from), parseDate(to))
        );

        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> getSpendingByCategory(String category, String from, String to) {
        List<Expense> expenses = expenseRepository.findAll(
                ExpenseSpecifications.withFilters(null, parseCategory(category), parseDate(from), parseDate(to))
        );

        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (ExpenseCategory expenseCategory : ExpenseCategory.values()) {
            totals.put(expenseCategory.name(), BigDecimal.ZERO);
        }

        for (Expense expense : expenses) {
            totals.merge(expense.getCategory().name(), expense.getAmount(), BigDecimal::add);
        }

        return totals;
    }

    public ExpenseResponse getMostExpensive(String category, String from, String to) {
        Specification<Expense> specification = ExpenseSpecifications.withFilters(
                null,
                parseCategory(category),
                parseDate(from),
                parseDate(to)
        );

        return expenseRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "amount"))
                .stream()
                .findFirst()
                .map(this::toResponse)
                .orElse(null);
    }

    private Expense findExpense(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }

    private ExpenseCategory parseCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        try {
            return ExpenseCategory.valueOf(category.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Category must be one of the supported categories");
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in yyyy-MM-dd format");
        }
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "expenseDate");
        }

        String normalized = sort.trim();

        if (normalized.equalsIgnoreCase("newest")) {
            return Sort.by(Sort.Direction.DESC, "expenseDate");
        }
        if (normalized.equalsIgnoreCase("oldest")) {
            return Sort.by(Sort.Direction.ASC, "expenseDate");
        }
        if (normalized.equalsIgnoreCase("highest amount")) {
            return Sort.by(Sort.Direction.DESC, "amount");
        }
        if (normalized.equalsIgnoreCase("lowest amount")) {
            return Sort.by(Sort.Direction.ASC, "amount");
        }

        String[] parts = normalized.split(",");
        String property = parts[0].trim().toLowerCase();
        Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.DESC;

        return switch (property) {
            case "amount" -> Sort.by(direction, "amount");
            case "date", "expenseDate" -> Sort.by(direction, "expenseDate");
            default -> Sort.by(Sort.Direction.DESC, "expenseDate");
        };
    }
}

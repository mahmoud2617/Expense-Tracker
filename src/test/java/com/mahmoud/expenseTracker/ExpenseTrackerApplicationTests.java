package com.mahmoud.expenseTracker;

import com.mahmoud.expenseTracker.category.ExpenseCategory;
import com.mahmoud.expenseTracker.dto.ExpenseRequest;
import com.mahmoud.expenseTracker.entity.Expense;
import com.mahmoud.expenseTracker.repository.ExpenseRepository;
import com.mahmoud.expenseTracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseTrackerApplicationTests {

    @Test
    void serviceCreatesExpenseFromRequest() {
        ExpenseRepository repository = Mockito.mock(ExpenseRepository.class);
        ExpenseService service = new ExpenseService(repository);

        ExpenseRequest request = new ExpenseRequest(
                BigDecimal.valueOf(250.00),
                ExpenseCategory.FOOD,
                "Dinner",
                LocalDate.of(2026, 9, 11)
        );

        Expense savedExpense = Expense.builder()
                .id(1L)
                .amount(request.amount())
                .category(request.category())
                .description(request.description())
                .expenseDate(request.expenseDate())
                .build();

        when(repository.save(any(Expense.class))).thenReturn(savedExpense);

        var result = service.createExpense(request);

        assertThat(result.description()).isEqualTo("Dinner");
        assertThat(result.amount()).isEqualByComparingTo("250.00");
        verify(repository).save(any(Expense.class));
    }
}

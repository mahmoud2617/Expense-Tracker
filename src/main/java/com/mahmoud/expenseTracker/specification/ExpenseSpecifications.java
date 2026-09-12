package com.mahmoud.expenseTracker.specification;

import com.mahmoud.expenseTracker.entity.Expense;
import com.mahmoud.expenseTracker.category.ExpenseCategory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class ExpenseSpecifications {

    private ExpenseSpecifications() {
    }

    public static Specification<Expense> withFilters(String search, ExpenseCategory category, LocalDate from, LocalDate to) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String normalizedSearch = search.trim().toLowerCase();
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + normalizedSearch + "%"));
            }

            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expenseDate"), from));
            }

            if (to != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expenseDate"), to));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

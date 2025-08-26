package com.vincent.InventoryManagementSystem.filter;

import com.vincent.InventoryManagementSystem.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionFilter {

    public static Specification<Transaction> byMonthAndYear(int month, int year) {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime end = start.plusMonths(1);

            return criteriaBuilder.between(root.get("createdAt"), start, end);
        };
    }
}

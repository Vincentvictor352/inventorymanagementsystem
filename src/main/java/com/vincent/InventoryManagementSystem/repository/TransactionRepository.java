package com.vincent.InventoryManagementSystem.repository;

import com.vincent.InventoryManagementSystem.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);


}

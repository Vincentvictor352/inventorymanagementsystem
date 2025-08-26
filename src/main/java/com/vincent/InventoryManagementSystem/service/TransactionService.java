package com.vincent.InventoryManagementSystem.service;

import com.vincent.InventoryManagementSystem.dto.Response;
import com.vincent.InventoryManagementSystem.dto.TransactionRequest;
import com.vincent.InventoryManagementSystem.enums.TransactionStatus;

public interface TransactionService {

    Response purchase(TransactionRequest transactionRequest);

    Response sell(TransactionRequest transactionRequest);

    Response returnToSupplier(TransactionRequest transactionRequest);

    Response getAllTransactions(int page, int size, String filter);

    Response getAllTransactionById(Long id);

    Response getAllTransactionByMonthAndYear(int month, int year);

    Response updateTransactionStatus(Long transactionId, TransactionStatus status);
}

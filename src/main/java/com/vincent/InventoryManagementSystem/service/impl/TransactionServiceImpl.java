package com.vincent.InventoryManagementSystem.service.impl;

import com.vincent.InventoryManagementSystem.dto.Response;
import com.vincent.InventoryManagementSystem.dto.TransactionDTO;
import com.vincent.InventoryManagementSystem.dto.TransactionRequest;
import com.vincent.InventoryManagementSystem.entity.Product;
import com.vincent.InventoryManagementSystem.entity.Supplier;
import com.vincent.InventoryManagementSystem.entity.Transaction;
import com.vincent.InventoryManagementSystem.entity.User;
import com.vincent.InventoryManagementSystem.enums.TransactionStatus;
import com.vincent.InventoryManagementSystem.enums.TransactionType;
import com.vincent.InventoryManagementSystem.exceptions.NotFoundException;
import com.vincent.InventoryManagementSystem.filter.TransactionFilter;
import com.vincent.InventoryManagementSystem.repository.ProductRepository;
import com.vincent.InventoryManagementSystem.repository.SupplierRepository;
import com.vincent.InventoryManagementSystem.repository.TransactionRepository;
import com.vincent.InventoryManagementSystem.service.TransactionService;
import com.vincent.InventoryManagementSystem.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response purchase(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        if (supplierId == null) throw new IllegalArgumentException("Supplier Id is required");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Supplier Not Found"));

        User user = userService.getCurrentLoggedInUser();

        // update stock
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);

        // create transaction
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.PURCHASE)
                .status(TransactionStatus.COMPLETED)
                .product(product)
                .user(user)
                .supplier(supplier)
                .totalProducts(quantity)
                .totalPrices(product.getPrice().multiply(BigDecimal.valueOf(quantity))) // ✅ renamed to totalPrice
                .description(transactionRequest.getDescription())
                .build();

        transactionRepository.save(transaction);

        return Response.builder()
                .status(200)
                .message("Purchase made successfully")
                .build();
    }

    @Override
    public Response sell(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Integer quantity = transactionRequest.getQuantity();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        User user = userService.getCurrentLoggedInUser();

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.SALE)
                .status(TransactionStatus.COMPLETED)
                .product(product)
                .user(user)
                .totalProducts(quantity)
                .totalPrices(BigDecimal.ZERO) // ✅
                .description(transactionRequest.getDescription())
                .build();

        transactionRepository.save(transaction);

        return Response.builder()
                .status(200)
                .message("Product sale successfully made")
                .build();
    }

    @Override
    public Response returnToSupplier(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        if (supplierId == null) throw new IllegalArgumentException("Supplier Id is required");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Supplier Not Found"));

        User user = userService.getCurrentLoggedInUser();

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available for return");
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.RETURN_TO_SUPPLIER)
                .status(TransactionStatus.PROCESSING)
                .product(product)
                .user(user)
                .supplier(supplier)
                .totalProducts(quantity)
                .totalPrices(BigDecimal.ZERO) // ✅ consistent naming
                .description(transactionRequest.getDescription())
                .build();

        transactionRepository.save(transaction);

        return Response.builder()
                .status(200)
                .message("Product return in progress")
                .build();
    }

    @Override
    public Response getAllTransactions(int page, int size, String filter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        List<TransactionDTO> transactionDTOS = modelMapper.map(
                transactionPage.getContent(),
                new TypeToken<List<TransactionDTO>>() {}.getType()
        );

        transactionDTOS.forEach(dto -> {
            if (dto.getUser() != null) dto.getUser().setTransactions(null);
            dto.setProduct(null);
            dto.setSupplier(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .transactions(transactionDTOS)
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .build();
    }

    @Override
    public Response getAllTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);

        if (transactionDTO.getUser() != null) {
            transactionDTO.getUser().setTransactions(null);
        }

        return Response.builder()
                .status(200)
                .message("success")
                .transaction(transactionDTO)
                .build();
    }

    @Override
    public Response getAllTransactionByMonthAndYear(int month, int year) {
        List<Transaction> transactions = transactionRepository.findAll(
                (Sort) TransactionFilter.byMonthAndYear(month, year)
        );

        List<TransactionDTO> transactionDTOS = modelMapper.map(
                transactions,
                new TypeToken<List<TransactionDTO>>() {}.getType()
        );

        transactionDTOS.forEach(dto -> {
            if (dto.getUser() != null) dto.getUser().setTransactions(null);
            dto.setProduct(null);
            dto.setSupplier(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .transactions(transactionDTOS)
                .build();
    }

    @Override
    public Response updateTransactionStatus(Long transactionId, TransactionStatus status) {
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        existingTransaction.setStatus(status);
        existingTransaction.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(existingTransaction);

        return Response.builder()
                .status(200)
                .message("Transaction status successfully updated")
                .build();
    }
}

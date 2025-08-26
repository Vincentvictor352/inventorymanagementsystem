package com.vincent.InventoryManagementSystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    @Column(unique = true)
    private String sku;

    @Positive(message = "Product price must be a positive value")
    private BigDecimal price;

    @Min(value = 10, message = "Stock quantity cannot be lesser than zero" )
    private Integer stockQuantity;

    private String description;
    private String imageUrl;

    private LocalDateTime expiryDate;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;



    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", SKU='" + sku + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", expiryDate=" + expiryDate +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}

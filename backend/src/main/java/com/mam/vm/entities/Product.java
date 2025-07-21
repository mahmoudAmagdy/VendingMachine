package com.mam.vm.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 100, message = "Product name must be between 1 and 100 characters")
    @Column(nullable = false)
    private String productName;
    
    @NotNull(message = "Amount available is required")
    @Min(value = 0, message = "Amount available must be non-negative")
    @Column(nullable = false)
    private Integer amountAvailable;
    
    @NotNull(message = "Cost is required")
    @Min(value = 5, message = "Cost must be at least 5 cents")
    @Column(nullable = false)
    private Integer cost;
    
    @NotNull(message = "Seller ID is required")
    @Column(nullable = false)
    private Long sellerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellerId", insertable = false, updatable = false)
    private User seller;
}
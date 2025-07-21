package com.mam.vm.repositories.rest.DTOs.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    @Size(min = 1, max = 100, message = "Product name must be between 1 and 100 characters")
    private String productName;
    
    @Min(value = 0, message = "Amount available must be non-negative")
    private Integer amountAvailable;
    
    @Min(value = 5, message = "Cost must be at least 5 cents")
    private Integer cost;
}
package com.mam.vm.repositories.rest.DTOs.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 100, message = "Product name must be between 1 and 100 characters")
    private String productName;
    
    @NotNull(message = "Amount available is required")
    @Min(value = 0, message = "Amount available must be non-negative")
    private Integer amountAvailable;
    
    @NotNull(message = "Cost is required")
    @Min(value = 5, message = "Cost must be at least 5 cents")
    private Integer cost;
}

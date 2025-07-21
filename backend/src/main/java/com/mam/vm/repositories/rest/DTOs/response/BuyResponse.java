package com.mam.vm.repositories.rest.DTOs.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyResponse {
    private Integer totalSpent;
    private ProductResponse product;
    private Integer amountPurchased;
    private Map<Integer, Integer> change;
}
package com.mam.vm.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mam.vm.entities.Coin;
import com.mam.vm.entities.Product;
import com.mam.vm.entities.User;
import com.mam.vm.repositories.rest.DTOs.request.BuyRequest;
import com.mam.vm.repositories.rest.DTOs.request.DepositRequest;
import com.mam.vm.repositories.rest.DTOs.response.BuyResponse;
import com.mam.vm.repositories.rest.DTOs.response.DepositResponse;
import com.mam.vm.repositories.rest.DTOs.response.ProductResponse;
import com.mam.vm.service.ProductService;
import com.mam.vm.service.UserService;
import com.mam.vm.service.VendingMachineService;
import com.mam.vm.service.exceptions.InsufficientFundsException;
import com.mam.vm.service.exceptions.InsufficientStockException;
import com.mam.vm.service.exceptions.InvalidOperationException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VendingMachineServiceImpl implements VendingMachineService {
    
    private final UserService userService;
    private final ProductService productService;
    
    @Override
    public DepositResponse deposit(DepositRequest request, Long userId) {
        log.info("Processing deposit of {} cents for user ID: {}", request.getCoin(), userId);
        
        // Validate coin value
        Coin.fromValue(request.getCoin()); // This will throw exception if invalid
        
        User user = userService.findUserEntityById(userId);
        Integer newDeposit = user.getDeposit() + request.getCoin();
        
        userService.updateUserDeposit(userId, newDeposit);
        
        log.info("Deposit successful. New deposit amount: {} cents", newDeposit);
        
        return DepositResponse.builder()
                .currentDeposit(newDeposit)
                .message("Deposit successful")
                .build();
    }
    
    @Override
    public BuyResponse buy(BuyRequest request, Long userId) {
        log.info("Processing purchase of {} units of product ID: {} for user ID: {}", 
                request.getAmount(), request.getProductId(), userId);
        
        User user = userService.findUserEntityById(userId);
        Product product = productService.findProductEntityById(request.getProductId());
        
        // Check if product is available
        if (product.getAmountAvailable() < request.getAmount()) {
            throw new InsufficientStockException("Insufficient stock. Available: " + 
                    product.getAmountAvailable() + ", Requested: " + request.getAmount());
        }
        
        // Calculate total cost
        Integer totalCost = product.getCost() * request.getAmount();
        
        // Check if user has enough money
        if (user.getDeposit() < totalCost) {
            throw new InsufficientFundsException("Insufficient funds. Required: " + 
                    totalCost + " cents, Available: " + user.getDeposit() + " cents");
        }
        
        // Calculate change
        Integer changeAmount = user.getDeposit() - totalCost;
        Map<Integer, Integer> change = calculateChange(changeAmount);
        
        // Update user deposit
        userService.updateUserDeposit(userId, 0);
        
        // Update product amount
        productService.updateProductAmount(request.getProductId(), 
                product.getAmountAvailable() - request.getAmount());
        
        log.info("Purchase successful. Total spent: {} cents, Change: {} cents", 
                totalCost, changeAmount);
        
        return BuyResponse.builder()
                .totalSpent(totalCost)
                .product(mapToProductResponse(product))
                .amountPurchased(request.getAmount())
                .change(change)
                .build();
    }
    
    @Override
    public DepositResponse reset(Long userId) {
        log.info("Resetting deposit for user ID: {}", userId);
        
        User user = userService.findUserEntityById(userId);
        Integer currentDeposit = user.getDeposit();
        
        if (currentDeposit == 0) {
            throw new InvalidOperationException("No deposit to reset");
        }
        
        userService.updateUserDeposit(userId, 0);
        
        log.info("Deposit reset successful. Returned: {} cents", currentDeposit);
        
        return DepositResponse.builder()
                .currentDeposit(0)
                .message("Deposit reset successful. Returned: " + currentDeposit + " cents")
                .build();
    }
    
    private Map<Integer, Integer> calculateChange(Integer changeAmount) {
        Map<Integer, Integer> change = new HashMap<>();
        
        // Available coin denominations in descending order
        int[] denominations = {100, 50, 20, 10, 5};
        
        for (int denomination : denominations) {
            if (changeAmount >= denomination) {
                int count = changeAmount / denomination;
                change.put(denomination, count);
                changeAmount -= count * denomination;
            }
        }
        
        return change;
    }
    
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .amountAvailable(product.getAmountAvailable())
                .cost(product.getCost())
                .sellerId(product.getSellerId())
                .build();
    }
}
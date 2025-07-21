package com.mam.vm.repositories.rest.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.mam.vm.entities.User;
import com.mam.vm.repositories.rest.DTOs.request.BuyRequest;
import com.mam.vm.repositories.rest.DTOs.request.DepositRequest;
import com.mam.vm.repositories.rest.DTOs.response.BuyResponse;
import com.mam.vm.repositories.rest.DTOs.response.DepositResponse;
import com.mam.vm.service.VendingMachineService;

@RestController
@RequestMapping("/api/vending")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('BUYER')")
public class VendingMachineController {
    
    private final VendingMachineService vendingMachineService;
    
    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(@Valid @RequestBody DepositRequest request, 
                                                  Authentication authentication) {
        User buyer = (User) authentication.getPrincipal();
        log.info("Deposit request from buyer: {} for amount: {}", buyer.getUsername(), request.getCoin());
        DepositResponse response = vendingMachineService.deposit(request, buyer.getId());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/buy")
    public ResponseEntity<BuyResponse> buy(@Valid @RequestBody BuyRequest request, 
                                          Authentication authentication) {
        User buyer = (User) authentication.getPrincipal();
        log.info("Buy request from buyer: {} for product: {} quantity: {}", 
                buyer.getUsername(), request.getProductId(), request.getAmount());
        BuyResponse response = vendingMachineService.buy(request, buyer.getId());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reset")
    public ResponseEntity<DepositResponse> reset(Authentication authentication) {
        User buyer = (User) authentication.getPrincipal();
        log.info("Reset request from buyer: {}", buyer.getUsername());
        DepositResponse response = vendingMachineService.reset(buyer.getId());
        return ResponseEntity.ok(response);
    }
    
    // Note: getBalance method removed as it's not in your VendingMachineService interface
    // Users can get their balance through the /api/users/me endpoint
}
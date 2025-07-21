package com.mam.vm.service;

import com.mam.vm.repositories.rest.DTOs.request.BuyRequest;
import com.mam.vm.repositories.rest.DTOs.request.DepositRequest;
import com.mam.vm.repositories.rest.DTOs.response.BuyResponse;
import com.mam.vm.repositories.rest.DTOs.response.DepositResponse;

public interface VendingMachineService {
    
    DepositResponse deposit(DepositRequest request, Long userId);
    
    BuyResponse buy(BuyRequest request, Long userId);
    
    DepositResponse reset(Long userId);
}
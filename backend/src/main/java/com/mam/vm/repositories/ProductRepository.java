package com.mam.vm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mam.vm.entities.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findBySellerId(Long sellerId);
    
    List<Product> findByAmountAvailableGreaterThan(Integer amount);
}
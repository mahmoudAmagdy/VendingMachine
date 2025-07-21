package com.mam.vm.repositories.rest.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.mam.vm.entities.User;
import com.mam.vm.repositories.rest.DTOs.request.ProductCreateRequest;
import com.mam.vm.repositories.rest.DTOs.request.ProductUpdateRequest;
import com.mam.vm.repositories.rest.DTOs.response.ProductResponse;
import com.mam.vm.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        log.info("Fetching all products");
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Long id) {
        log.info("Fetching product with ID: {}", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request, 
                                                        Authentication authentication) {
        User seller = (User) authentication.getPrincipal();
        log.info("Creating product for seller: {}", seller.getUsername());
        ProductResponse product = productService.createProduct(request, seller.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("id") Long id, 
                                                        @Valid @RequestBody ProductUpdateRequest request,
                                                        Authentication authentication) {
        User seller = (User) authentication.getPrincipal();
        log.info("Updating product with ID: {} by seller: {}", id, seller.getUsername());
        ProductResponse product = productService.updateProduct(id, request, seller.getId());
        return ResponseEntity.ok(product);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id, Authentication authentication) {
        User seller = (User) authentication.getPrincipal();
        log.info("Deleting product with ID: {} by seller: {}", id, seller.getUsername());
        productService.deleteProduct(id, seller.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ProductResponse>> getProductsBySeller(@PathVariable Long sellerId) {
        log.info("Fetching products for seller ID: {}", sellerId);
        List<ProductResponse> products = productService.getProductsBySellerId(sellerId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Authentication authentication) {
        User seller = (User) authentication.getPrincipal();
        log.info("Fetching products for seller: {}", seller.getUsername());
        List<ProductResponse> products = productService.getProductsBySellerId(seller.getId());
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {
        log.info("Fetching available products");
        List<ProductResponse> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }
}
package com.mam.vm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mam.vm.entities.Product;
import com.mam.vm.repositories.rest.DTOs.request.ProductCreateRequest;
import com.mam.vm.repositories.rest.DTOs.request.ProductUpdateRequest;
import com.mam.vm.repositories.rest.DTOs.response.ProductResponse;

import java.util.List;

public interface ProductService {
    
    ProductResponse createProduct(ProductCreateRequest request, Long sellerId);
    
    ProductResponse getProductById(Long id);
    
    Page<ProductResponse> getAllProducts(Pageable pageable);
    
    List<ProductResponse> getProductsBySellerId(Long sellerId);
    
    List<ProductResponse> getAvailableProducts();
    
    ProductResponse updateProduct(Long id, ProductUpdateRequest request, Long sellerId);
    
    void deleteProduct(Long id, Long sellerId);
    
    Product findProductEntityById(Long id);
    
    void updateProductAmount(Long productId, Integer newAmount);
}
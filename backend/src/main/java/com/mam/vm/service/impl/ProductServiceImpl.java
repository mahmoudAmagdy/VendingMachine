package com.mam.vm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mam.vm.entities.Product;
import com.mam.vm.repositories.ProductRepository;
import com.mam.vm.repositories.rest.DTOs.request.ProductCreateRequest;
import com.mam.vm.repositories.rest.DTOs.request.ProductUpdateRequest;
import com.mam.vm.repositories.rest.DTOs.response.ProductResponse;
import com.mam.vm.service.ProductService;
import com.mam.vm.service.exceptions.ResourceNotFoundException;
import com.mam.vm.service.exceptions.UnauthorizedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    @Override
    public ProductResponse createProduct(ProductCreateRequest request, Long sellerId) {
        log.info("Creating product '{}' for seller ID: {}", request.getProductName(), sellerId);
        
        Product product = Product.builder()
                .productName(request.getProductName())
                .amountAvailable(request.getAmountAvailable())
                .cost(request.getCost())
                .sellerId(sellerId)
                .build();
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        
        return mapToProductResponse(savedProduct);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        Product product = findProductEntityById(id);
        return mapToProductResponse(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Fetching all products with pagination");
        return productRepository.findAll(pageable)
                .map(this::mapToProductResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsBySellerId(Long sellerId) {
        log.info("Fetching products for seller ID: {}", sellerId);
        return productRepository.findBySellerId(sellerId)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProducts() {
        log.info("Fetching available products");
        return productRepository.findByAmountAvailableGreaterThan(0)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request, Long sellerId) {
        log.info("Updating product with ID: {} by seller ID: {}", id, sellerId);
        
        Product product = findProductEntityById(id);
        
        if (!product.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("You can only update your own products");
        }
        
        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
        }
        
        if (request.getAmountAvailable() != null) {
            product.setAmountAvailable(request.getAmountAvailable());
        }
        
        if (request.getCost() != null) {
            product.setCost(request.getCost());
        }
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        
        return mapToProductResponse(updatedProduct);
    }
    
    @Override
    public void deleteProduct(Long id, Long sellerId) {
        log.info("Deleting product with ID: {} by seller ID: {}", id, sellerId);
        
        Product product = findProductEntityById(id);
        
        if (!product.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("You can only delete your own products");
        }
        
        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Product findProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }
    
    @Override
    public void updateProductAmount(Long productId, Integer newAmount) {
        log.info("Updating amount for product ID: {} to: {}", productId, newAmount);
        
        Product product = findProductEntityById(productId);
        product.setAmountAvailable(newAmount);
        productRepository.save(product);
        
        log.info("Product amount updated successfully for product ID: {}", productId);
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
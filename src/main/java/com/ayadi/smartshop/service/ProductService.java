package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.CreateProductRequest;
import com.ayadi.smartshop.dto.request.UpdateProductRequest;
import com.ayadi.smartshop.dto.response.ProductResponse;
import com.ayadi.smartshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse getProduct(Long id);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductResponse> searchProducts(String name, Pageable pageable);
    ProductResponse updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);
    Product findProductById(Long id);
}
package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.CreateProductRequest;
import com.ayadi.smartshop.dto.request.UpdateProductRequest;
import com.ayadi.smartshop.dto.response.ProductResponse;
import com.ayadi.smartshop.entity.Product;
import com.ayadi.smartshop.exception.BusinessException;
import com.ayadi.smartshop.exception.ResourceNotFoundException;
import com.ayadi.smartshop.mapper.ProductMapper;
import com.ayadi.smartshop.repository.OrderItemRepository;
import com.ayadi.smartshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;
    
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }
    
    public ProductResponse getProduct(Long id) {
        Product product = findProductById(id);
        if (product.getDeleted()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return productMapper.toResponse(product);
    }
    
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAllActive(pageable)
                .map(productMapper::toResponse);
    }
    
    public Page<ProductResponse> searchProducts(String name, Pageable pageable) {
        return productRepository.findByNameContainingAndDeletedFalse(name, pageable)
                .map(productMapper::toResponse);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductById(id);
        
        if (product.getDeleted()) {
            throw new BusinessException("Cannot update deleted product");
        }
        
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        
        if (orderItemRepository.existsByProductId(id)) {
            product.setDeleted(true);
            productRepository.save(product);
        } else {
            productRepository.delete(product);
        }
    }
    
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
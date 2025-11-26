package com.ayadi.smartshop.mapper;

import com.ayadi.smartshop.dto.request.CreateProductRequest;
import com.ayadi.smartshop.dto.request.UpdateProductRequest;
import com.ayadi.smartshop.dto.response.ProductResponse;
import com.ayadi.smartshop.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toResponse(Product product);
    Product toEntity(CreateProductRequest request);
    Product toEntity(UpdateProductRequest request);
}
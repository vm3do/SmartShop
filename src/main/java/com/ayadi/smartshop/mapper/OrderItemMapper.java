package com.ayadi.smartshop.mapper;

import com.ayadi.smartshop.dto.response.OrderItemResponse;
import com.ayadi.smartshop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toResponse(OrderItem orderItem);
}
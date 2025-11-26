package com.ayadi.smartshop.mapper;

import com.ayadi.smartshop.dto.response.OrderResponse;
import com.ayadi.smartshop.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    OrderResponse toResponse(Order order);
}
package com.ayadi.smartshop.mapper;

import com.ayadi.smartshop.dto.request.CreatePaymentRequest;
import com.ayadi.smartshop.dto.response.PaymentResponse;
import com.ayadi.smartshop.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    @Mapping(source = "order.id", target = "orderId")
    PaymentResponse toResponse(Payment payment);
    
    @Mapping(source = "orderId", target = "order.id")
    Payment toEntity(CreatePaymentRequest request);
}
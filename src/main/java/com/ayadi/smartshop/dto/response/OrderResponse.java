package com.ayadi.smartshop.dto.response;

import com.ayadi.smartshop.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private LocalDateTime createdAt;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal subtotalAfterDiscount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String promoCode;
    private OrderStatus status;
    private BigDecimal remainingAmount;
    private List<OrderItemResponse> orderItems;
}
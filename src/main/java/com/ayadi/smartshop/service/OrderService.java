package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.CreateOrderRequest;
import com.ayadi.smartshop.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrder(Long id);
    List<OrderResponse> getClientOrders(Long clientId);
    OrderResponse confirmOrder(Long id);
    OrderResponse cancelOrder(Long id);
}
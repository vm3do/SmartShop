package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.CreatePaymentRequest;
import com.ayadi.smartshop.dto.response.PaymentResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    PaymentResponse addPayment(CreatePaymentRequest request);
    List<PaymentResponse> getOrderPayments(Long orderId);
    BigDecimal calculateTotalPaid(Long orderId);
    BigDecimal calculateRemainingAmount(Long orderId);
}
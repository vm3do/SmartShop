package com.ayadi.smartshop.service.impl;

import com.ayadi.smartshop.dto.request.CreatePaymentRequest;
import com.ayadi.smartshop.dto.response.PaymentResponse;
import com.ayadi.smartshop.entity.Order;
import com.ayadi.smartshop.entity.Payment;
import com.ayadi.smartshop.enums.OrderStatus;
import com.ayadi.smartshop.enums.PaymentStatus;
import com.ayadi.smartshop.enums.PaymentType;
import com.ayadi.smartshop.exception.BusinessException;
import com.ayadi.smartshop.exception.ResourceNotFoundException;
import com.ayadi.smartshop.mapper.PaymentMapper;
import com.ayadi.smartshop.repository.OrderRepository;
import com.ayadi.smartshop.repository.PaymentRepository;
import com.ayadi.smartshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    
    private static final BigDecimal CASH_LIMIT = new BigDecimal("20000");
    
    @Override
    @Transactional
    public PaymentResponse addPayment(CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Cannot add payment to order with status: " + order.getStatus());
        }
        
        BigDecimal remainingAmount = calculateRemainingAmount(request.getOrderId());
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new BusinessException("Payment amount exceeds remaining order amount");
        }
        
        if (request.getPaymentType() == PaymentType.ESPECES && request.getAmount().compareTo(CASH_LIMIT) > 0) {
            throw new BusinessException("Cash payment cannot exceed " + CASH_LIMIT + " DH");
        }
        
        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .paymentType(request.getPaymentType())
                .status(PaymentStatus.EN_ATTENTE)
                .reference(request.getReference())
                .bank(request.getBank())
                .dueDate(request.getDueDate())
                .build();
        
        payment = paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }
    
    @Override
    public List<PaymentResponse> getOrderPayments(Long orderId) {
        return paymentRepository.findByOrderIdOrderByPaymentNumber(orderId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
    
    @Override
    public BigDecimal calculateTotalPaid(Long orderId) {
        BigDecimal totalPaid = paymentRepository.sumAmountByOrderIdAndStatus(orderId, PaymentStatus.ENCAISSE);
        return totalPaid != null ? totalPaid : BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal calculateRemainingAmount(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        BigDecimal totalPaid = calculateTotalPaid(orderId);
        return order.getTotalAmount().subtract(totalPaid);
    }
}
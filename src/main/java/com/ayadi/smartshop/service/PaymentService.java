package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.CreatePaymentRequest;
import com.ayadi.smartshop.dto.response.PaymentResponse;
import com.ayadi.smartshop.entity.Order;
import com.ayadi.smartshop.entity.Payment;
import com.ayadi.smartshop.enums.OrderStatus;
import com.ayadi.smartshop.enums.PaymentStatus;
import com.ayadi.smartshop.enums.PaymentType;
import com.ayadi.smartshop.exception.BusinessException;
import com.ayadi.smartshop.mapper.PaymentMapper;
import com.ayadi.smartshop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final PaymentMapper paymentMapper;
    
    private static final BigDecimal CASH_LIMIT = new BigDecimal("20000");
    
    @Transactional
    public PaymentResponse addPayment(CreatePaymentRequest request) {
        Order order = orderService.findOrderById(request.getOrderId());
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Can only add payments to pending orders");
        }
        
        if (request.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new BusinessException("Payment amount exceeds remaining amount");
        }
        
        if (request.getPaymentType() == PaymentType.ESPECES && 
            request.getAmount().compareTo(CASH_LIMIT) > 0) {
            throw new BusinessException("Cash payment cannot exceed 20,000 DH");
        }
        
        Integer nextPaymentNumber = paymentRepository.findMaxPaymentNumberByOrderId(order.getId());
        nextPaymentNumber = (nextPaymentNumber == null) ? 1 : nextPaymentNumber + 1;
        
        Payment payment = paymentMapper.toEntity(request);
        payment.setPaymentNumber(nextPaymentNumber);
        
        if (request.getPaymentType() == PaymentType.ESPECES) {
            payment.setStatus(PaymentStatus.ENCAISSE);
        } else {
            payment.setStatus(PaymentStatus.EN_ATTENTE);
        }
        
        payment = paymentRepository.save(payment);
        
        if (payment.getStatus() == PaymentStatus.ENCAISSE) {
            order.setRemainingAmount(order.getRemainingAmount().subtract(payment.getAmount()));
            orderService.findOrderById(order.getId());
        }
        
        return paymentMapper.toResponse(payment);
    }
    
    public List<PaymentResponse> getOrderPayments(Long orderId) {
        return paymentRepository.findByOrderIdOrderByPaymentNumber(orderId).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
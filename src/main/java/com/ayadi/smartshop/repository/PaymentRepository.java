package com.ayadi.smartshop.repository;

import com.ayadi.smartshop.entity.Payment;
import com.ayadi.smartshop.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByOrderIdOrderByPaymentNumber(Long orderId);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order.id = :orderId AND p.status = :status")
    BigDecimal sumAmountByOrderIdAndStatus(Long orderId, PaymentStatus status);
    
    @Query("SELECT MAX(p.paymentNumber) FROM Payment p WHERE p.order.id = :orderId")
    Integer findMaxPaymentNumberByOrderId(Long orderId);
}
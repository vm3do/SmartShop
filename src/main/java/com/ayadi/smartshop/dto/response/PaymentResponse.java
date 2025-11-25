package com.ayadi.smartshop.dto.response;

import com.ayadi.smartshop.enums.PaymentStatus;
import com.ayadi.smartshop.enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Integer paymentNumber;
    private BigDecimal amount;
    private PaymentType paymentType;
    private LocalDateTime paymentDate;
    private LocalDate collectionDate;
    private PaymentStatus status;
    private String reference;
    private String bank;
    private LocalDate dueDate;
}
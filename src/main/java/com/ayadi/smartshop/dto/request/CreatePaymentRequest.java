package com.ayadi.smartshop.dto.request;

import com.ayadi.smartshop.enums.PaymentType;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreatePaymentRequest {
    @NotNull
    private Long orderId;
    
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    
    @NotNull
    private PaymentType paymentType;
    
    private String reference;
    private String bank;
    private LocalDate dueDate;
}
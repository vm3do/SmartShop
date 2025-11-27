package com.ayadi.smartshop.controller;

import com.ayadi.smartshop.dto.request.CreatePaymentRequest;
import com.ayadi.smartshop.dto.response.PaymentResponse;
import com.ayadi.smartshop.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<PaymentResponse> addPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.addPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getOrderPayments(@PathVariable Long orderId) {
        List<PaymentResponse> response = paymentService.getOrderPayments(orderId);
        return ResponseEntity.ok(response);
    }
}
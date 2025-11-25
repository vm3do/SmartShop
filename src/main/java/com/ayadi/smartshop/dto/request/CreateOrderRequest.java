package com.ayadi.smartshop.dto.request;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long clientId;
    
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Promo code must follow format PROMO-XXXX")
    private String promoCode;
    
    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;
    
    @Data
    public static class OrderItemRequest {
        @NotNull
        private Long productId;
        
        @NotNull
        private Integer quantity;
    }
}
package com.ayadi.smartshop.dto.response;

import com.ayadi.smartshop.enums.CustomerTier;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClientResponse {
    private Long id;
    private String name;
    private String email;
    private CustomerTier tier;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;
}
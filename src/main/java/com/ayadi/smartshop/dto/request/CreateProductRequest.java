package com.ayadi.smartshop.dto.request;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    @NotBlank
    private String name;
    
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
    
    @NotNull
    @Min(0)
    private Integer stock;
}
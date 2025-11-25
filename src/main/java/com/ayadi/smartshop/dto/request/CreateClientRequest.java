package com.ayadi.smartshop.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateClientRequest {
    @NotBlank
    private String name;
    
    @Email
    @NotBlank
    private String email;
}
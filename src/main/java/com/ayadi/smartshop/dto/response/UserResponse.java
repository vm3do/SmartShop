package com.ayadi.smartshop.dto.response;

import com.ayadi.smartshop.enums.UserRole;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private UserRole role;
}
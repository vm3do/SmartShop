package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.LoginRequest;
import com.ayadi.smartshop.dto.response.UserResponse;
import com.ayadi.smartshop.entity.User;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    UserResponse login(LoginRequest request, HttpSession session);
    void logout(HttpSession session);
    User getCurrentUser(HttpSession session);
    UserResponse getCurrentUserResponse(HttpSession session);
}
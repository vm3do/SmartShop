package com.ayadi.smartshop.service.impl;

import com.ayadi.smartshop.dto.request.LoginRequest;
import com.ayadi.smartshop.dto.response.UserResponse;
import com.ayadi.smartshop.entity.User;
import com.ayadi.smartshop.exception.UnauthorizedException;
import com.ayadi.smartshop.mapper.UserMapper;
import com.ayadi.smartshop.repository.UserRepository;
import com.ayadi.smartshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Override
    public UserResponse login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        
        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }
        
        session.setAttribute("user", user);
        return userMapper.toResponse(user);
    }
    
    @Override
    public void logout(HttpSession session) {
        session.invalidate();
    }
    
    @Override
    public User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        return user;
    }
    
    @Override
    public UserResponse getCurrentUserResponse(HttpSession session) {
        User user = getCurrentUser(session);
        return userMapper.toResponse(user);
    }
}
package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.LoginRequest;
import com.ayadi.smartshop.dto.response.UserResponse;
import com.ayadi.smartshop.entity.User;
import com.ayadi.smartshop.exception.UnauthorizedException;
import com.ayadi.smartshop.mapper.UserMapper;
import com.ayadi.smartshop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public UserResponse login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        
        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }
        
        session.setAttribute("user", user);
        return userMapper.toResponse(user);
    }
    
    public void logout(HttpSession session) {
        session.invalidate();
    }
    
    public User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        return user;
    }
}
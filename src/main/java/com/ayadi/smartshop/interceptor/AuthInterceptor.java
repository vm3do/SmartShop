package com.ayadi.smartshop.interceptor;

import com.ayadi.smartshop.entity.User;
import com.ayadi.smartshop.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            throw new UnauthorizedException("Authentication required");
        }
        
        return true;
    }
}
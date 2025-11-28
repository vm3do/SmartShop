package com.ayadi.smartshop.interceptor;

import com.ayadi.smartshop.entity.User;
import com.ayadi.smartshop.enums.UserRole;
import com.ayadi.smartshop.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null && user.getRole() == UserRole.ADMIN) {
                return true;
            }
        }
        
        throw new ForbiddenException("Admin access required");
    }
}
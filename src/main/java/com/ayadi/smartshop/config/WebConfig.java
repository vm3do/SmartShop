package com.ayadi.smartshop.config;

import com.ayadi.smartshop.interceptor.AdminInterceptor;
import com.ayadi.smartshop.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login");
        
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/api/clients/**",
                        "/api/products/**",
                        "/api/orders/*/confirm",
                        "/api/orders/*/cancel",
                        "/api/payments/**"
                );
    }
}
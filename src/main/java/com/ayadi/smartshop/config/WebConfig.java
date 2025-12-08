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
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                );
        
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/clients/**",
                        "/products/**",
                        "/orders/*/confirm",
                        "/orders/*/cancel",
                        "/payments/**"
                );
    }
}
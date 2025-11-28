package com.ayadi.smartshop.config;

import com.ayadi.smartshop.entity.Client;
import com.ayadi.smartshop.entity.Product;
import com.ayadi.smartshop.entity.User;
import com.ayadi.smartshop.enums.UserRole;
import com.ayadi.smartshop.repository.ClientRepository;
import com.ayadi.smartshop.repository.ProductRepository;
import com.ayadi.smartshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password("admin123")
                    .role(UserRole.ADMIN)
                    .build();
            userRepository.save(admin);
            
            User client = User.builder()
                    .username("client")
                    .password("client123")
                    .role(UserRole.CLIENT)
                    .build();
            userRepository.save(client);
        }
        
        if (clientRepository.count() == 0) {
            for (int i = 1; i <= 5; i++) {
                Client client = Client.builder()
                        .name("Client " + i)
                        .email("client" + i + "@example.com")
                        .build();
                clientRepository.save(client);
            }
        }
        
        if (productRepository.count() == 0) {
            productRepository.save(Product.builder()
                    .name("Laptop Dell XPS 15")
                    .price(new BigDecimal("15000.00"))
                    .stock(50)
                    .build());
            
            productRepository.save(Product.builder()
                    .name("HP Printer LaserJet")
                    .price(new BigDecimal("3500.00"))
                    .stock(30)
                    .build());
            
            productRepository.save(Product.builder()
                    .name("Logitech Mouse MX Master")
                    .price(new BigDecimal("850.00"))
                    .stock(100)
                    .build());
            
            productRepository.save(Product.builder()
                    .name("Samsung Monitor 27\"")
                    .price(new BigDecimal("2500.00"))
                    .stock(40)
                    .build());
            
            productRepository.save(Product.builder()
                    .name("Mechanical Keyboard RGB")
                    .price(new BigDecimal("1200.00"))
                    .stock(60)
                    .build());
        }
    }
}
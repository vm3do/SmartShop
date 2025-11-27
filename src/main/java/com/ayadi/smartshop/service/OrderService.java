package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.CreateOrderRequest;
import com.ayadi.smartshop.dto.response.OrderResponse;
import com.ayadi.smartshop.entity.Client;
import com.ayadi.smartshop.entity.Order;
import com.ayadi.smartshop.entity.OrderItem;
import com.ayadi.smartshop.entity.Product;
import com.ayadi.smartshop.enums.CustomerTier;
import com.ayadi.smartshop.enums.OrderStatus;
import com.ayadi.smartshop.exception.BusinessException;
import com.ayadi.smartshop.exception.ResourceNotFoundException;
import com.ayadi.smartshop.mapper.OrderMapper;
import com.ayadi.smartshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final OrderMapper orderMapper;
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.20");
    private static final BigDecimal PROMO_DISCOUNT = new BigDecimal("0.05");
    
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Client client = clientService.findClientById(request.getClientId());
        
        Order order = Order.builder()
                .client(client)
                .createdAt(LocalDateTime.now())
                .promoCode(request.getPromoCode())
                .status(OrderStatus.PENDING)
                .build();
        
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.findProductById(itemRequest.getProductId());
            
            if (product.getDeleted()) {
                throw new BusinessException("Product is no longer available: " + product.getName());
            }
            
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }
            
            BigDecimal itemTotal = product.getPrice()
                    .multiply(new BigDecimal(itemRequest.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .lineTotal(itemTotal)
                    .build();
            
            orderItems.add(orderItem);
            subtotal = subtotal.add(itemTotal);
        }
        
        order.setSubtotal(subtotal);
        order.setOrderItems(orderItems);
        
        BigDecimal discountAmount = calculateDiscount(subtotal, client.getTier(), request.getPromoCode());
        order.setDiscountAmount(discountAmount);
        
        BigDecimal subtotalAfterDiscount = subtotal.subtract(discountAmount);
        order.setSubtotalAfterDiscount(subtotalAfterDiscount);
        
        BigDecimal taxAmount = subtotalAfterDiscount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        order.setTaxAmount(taxAmount);
        
        BigDecimal totalAmount = subtotalAfterDiscount.add(taxAmount);
        order.setTotalAmount(totalAmount);
        order.setRemainingAmount(totalAmount);
        
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }
    
    private BigDecimal calculateDiscount(BigDecimal subtotal, CustomerTier tier, String promoCode) {
        BigDecimal loyaltyDiscount = BigDecimal.ZERO;
        
        switch (tier) {
            case SILVER:
                if (subtotal.compareTo(new BigDecimal("500")) >= 0) {
                    loyaltyDiscount = subtotal.multiply(new BigDecimal("0.05"));
                }
                break;
            case GOLD:
                if (subtotal.compareTo(new BigDecimal("800")) >= 0) {
                    loyaltyDiscount = subtotal.multiply(new BigDecimal("0.10"));
                }
                break;
            case PLATINUM:
                if (subtotal.compareTo(new BigDecimal("1200")) >= 0) {
                    loyaltyDiscount = subtotal.multiply(new BigDecimal("0.15"));
                }
                break;
        }
        
        BigDecimal promoDiscount = BigDecimal.ZERO;
        if (promoCode != null && promoCode.matches("PROMO-[A-Z0-9]{4}")) {
            promoDiscount = subtotal.multiply(PROMO_DISCOUNT);
        }
        
        return loyaltyDiscount.add(promoDiscount).setScale(2, RoundingMode.HALF_UP);
    }
    
    public OrderResponse getOrder(Long id) {
        Order order = findOrderById(id);
        return orderMapper.toResponse(order);
    }
    
    public List<OrderResponse> getClientOrders(Long clientId) {
        return orderRepository.findByClientIdOrderByCreatedAtDesc(clientId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }
    
    @Transactional
    public OrderResponse confirmOrder(Long id) {
        Order order = findOrderById(id);
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only pending orders can be confirmed");
        }
        
        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Order must be fully paid before confirmation");
        }
        
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
        }
        
        Client client = order.getClient();
        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(order.getTotalAmount()));
        
        if (client.getFirstOrderDate() == null) {
            client.setFirstOrderDate(order.getCreatedAt());
        }
        client.setLastOrderDate(order.getCreatedAt());
        
        order.setStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);
        
        clientService.updateClientTier(client.getId());
        
        return orderMapper.toResponse(order);
    }
    
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = findOrderById(id);
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only pending orders can be canceled");
        }
        
        order.setStatus(OrderStatus.CANCELED);
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }
    
    public Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
}
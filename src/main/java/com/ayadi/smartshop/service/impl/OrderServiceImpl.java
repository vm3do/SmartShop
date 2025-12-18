package com.ayadi.smartshop.service.impl;

import com.ayadi.smartshop.dto.request.CreateOrderRequest;
import com.ayadi.smartshop.dto.response.OrderResponse;
import com.ayadi.smartshop.entity.*;
import com.ayadi.smartshop.enums.CustomerTier;
import com.ayadi.smartshop.enums.OrderStatus;
import com.ayadi.smartshop.exception.BusinessException;
import com.ayadi.smartshop.exception.ResourceNotFoundException;
import com.ayadi.smartshop.mapper.OrderMapper;
import com.ayadi.smartshop.repository.OrderRepository;
import com.ayadi.smartshop.service.ClientService;
import com.ayadi.smartshop.service.OrderService;
import com.ayadi.smartshop.service.PaymentService;
import com.ayadi.smartshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.20");
    private static final Pattern PROMO_PATTERN = Pattern.compile("PROMO-[A-Z0-9]{4}");
    
    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Client client = clientService.findClientById(request.getClientId());
        
        validateStock(request.getItems());
        
        BigDecimal subtotal = calculateSubtotal(request.getItems());
        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(client, subtotal);
        BigDecimal promoDiscount = calculatePromoDiscount(request.getPromoCode(), subtotal);
        BigDecimal discountedAmount = subtotal.subtract(loyaltyDiscount).subtract(promoDiscount);
        BigDecimal taxAmount = discountedAmount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = discountedAmount.add(taxAmount);
        
        Order order = Order.builder()
                .client(client)
                .subtotal(subtotal)
                .discountAmount(loyaltyDiscount.add(promoDiscount))
                .subtotalAfterDiscount(discountedAmount)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .remainingAmount(totalAmount)
                .promoCode(request.getPromoCode())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.findProductById(itemRequest.getProductId());
            BigDecimal lineTotal = product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .lineTotal(lineTotal)
                    .build();
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }
    
    @Override
    public OrderResponse getOrder(Long id) {
        Order order = findOrderById(id);
        return orderMapper.toResponse(order);
    }
    
    @Override
    public List<OrderResponse> getClientOrders(Long clientId) {
        return orderRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional
    public OrderResponse confirmOrder(Long id) {
        Order order = findOrderById(id);
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Order must be in PENDING status to confirm");
        }
        
        BigDecimal totalPaid = paymentService.calculateTotalPaid(id);
        if (totalPaid.compareTo(order.getTotalAmount()) < 0) {
            throw new BusinessException("Order must be fully paid before confirmation");
        }
        
        decrementStock(order);
        updateClientStats(order.getClient());
        
        order.setStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);
        
        return orderMapper.toResponse(order);
    }
    
    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = findOrderById(id);
        
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessException("Cannot cancel order with status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELED);
        order = orderRepository.save(order);
        
        return orderMapper.toResponse(order);
    }
    
    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
    
    private void validateStock(List<CreateOrderRequest.OrderItemRequest> items) {
        for (CreateOrderRequest.OrderItemRequest item : items) {
            Product product = productService.findProductById(item.getProductId());
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }
        }
    }
    
    private BigDecimal calculateSubtotal(List<CreateOrderRequest.OrderItemRequest> items) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest item : items) {
            Product product = productService.findProductById(item.getProductId());
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateLoyaltyDiscount(Client client, BigDecimal subtotal) {
        CustomerTier tier = client.getTier();
        
        return switch (tier) {
            case SILVER -> subtotal.compareTo(new BigDecimal("500")) >= 0 ? 
                subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            case GOLD -> subtotal.compareTo(new BigDecimal("800")) >= 0 ? 
                subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            case PLATINUM -> subtotal.compareTo(new BigDecimal("1200")) >= 0 ? 
                subtotal.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            default -> BigDecimal.ZERO;
        };
    }
    
    private BigDecimal calculatePromoDiscount(String promoCode, BigDecimal subtotal) {
        if (promoCode != null && PROMO_PATTERN.matcher(promoCode).matches()) {
            return subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    private void decrementStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
        }
    }
    
    private void updateClientStats(Client client) {
        Long totalOrders = orderRepository.countByClientIdAndStatus(client.getId(), OrderStatus.CONFIRMED);
        BigDecimal totalSpent = orderRepository.sumTotalByClientIdAndStatus(client.getId(), OrderStatus.CONFIRMED);
        
        client.setTotalOrders(totalOrders.intValue());
        client.setTotalSpent(totalSpent);
        client.setTier(calculateLoyaltyTier(totalOrders.intValue(), totalSpent));
    }
    
    private CustomerTier calculateLoyaltyTier(int totalOrders, BigDecimal totalSpent) {
        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            return CustomerTier.PLATINUM;
        } else if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            return CustomerTier.GOLD;
        } else if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return CustomerTier.SILVER;
        }
        return CustomerTier.BASIC;
    }
}
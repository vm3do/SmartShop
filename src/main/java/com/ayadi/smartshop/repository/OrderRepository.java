package com.ayadi.smartshop.repository;

import com.ayadi.smartshop.entity.Order;
import com.ayadi.smartshop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByClientIdOrderByCreatedAtDesc(Long clientId);
    
    Page<Order> findByClientId(Long clientId, Pageable pageable);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.client.id = :clientId AND o.status = :status")
    Long countByClientIdAndStatus(Long clientId, OrderStatus status);
    
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.client.id = :clientId AND o.status = :status")
    BigDecimal sumTotalByClientIdAndStatus(Long clientId, OrderStatus status);
}
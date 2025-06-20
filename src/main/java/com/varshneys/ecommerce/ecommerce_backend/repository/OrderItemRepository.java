package com.varshneys.ecommerce.ecommerce_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.Order;
import com.varshneys.ecommerce.ecommerce_backend.Model.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order = :order")
    List<OrderItem> findByOrder(@Param("order") Order order);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.productId = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);
    
    @Query("DELETE FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);
}

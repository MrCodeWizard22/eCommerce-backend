package com.varshneys.ecommerce.ecommerce_backend.repository;
import  java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.user.id = :userId ORDER BY o.orderId DESC")
    List<Order> findAllOrdersByUserId(@Param("userId") Long userId);

    // Custom query to find all orders by seller ID
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p WHERE p.seller.userId = :sellerId ORDER BY o.orderId DESC")
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);

    @Query("Select o from Order o")
    List<Order> findAllOrder();
    
}

package com.varshneys.ecommerce.ecommerce_backend.repository;
import  java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findAllOrdersByUserId(@Param("userId") Long userId);

    // Custom query to find all orders by seller ID
    @Query("Select o from Order o")
    List<Order> findAllOrder(); 
    
}

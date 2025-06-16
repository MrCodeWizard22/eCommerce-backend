package com.varshneys.ecommerce.ecommerce_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varshneys.ecommerce.ecommerce_backend.Model.Order;
import com.varshneys.ecommerce.ecommerce_backend.repository.OrderRepository;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    public void updateOrderStatus(Long orderId, String status) {
        // This is a placeholder implementation
        // You'll need to implement the actual order status update logic
        System.out.println("Order " + orderId + " status updated to: " + status);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAllOrder();
    }
}

package com.varshneys.ecommerce.ecommerce_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varshneys.ecommerce.ecommerce_backend.Model.Order;
import com.varshneys.ecommerce.ecommerce_backend.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    //get all orders
    public List<Order> getAllOrders() {
        return (List<Order>) orderRepository.findAllOrder();
    }

}

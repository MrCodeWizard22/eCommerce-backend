package com.varshneys.ecommerce.ecommerce_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varshneys.ecommerce.ecommerce_backend.Model.Order;
import com.varshneys.ecommerce.ecommerce_backend.Model.Product;
import com.varshneys.ecommerce.ecommerce_backend.Model.Role;
import com.varshneys.ecommerce.ecommerce_backend.Model.User;
import com.varshneys.ecommerce.ecommerce_backend.repository.UserRepository;

@Service
public class AdminService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductService productService;

    // get all user 
    public List<User> getAllUsers() {
        return userRepository.findAllUsers().stream().filter(user -> user.getRole()== Role.USER).toList();
    }
    
    // get all sellers 
    public List<User> getAllSellers() {
        return userRepository.findAllUsers().stream().filter(user -> user.getRole()==Role.SELLER).toList();
    }
    
    // get all orders
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    
    // get all products
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
}

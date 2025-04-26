package com.varshneys.ecommerce.ecommerce_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.varshneys.ecommerce.ecommerce_backend.Model.Order;
import com.varshneys.ecommerce.ecommerce_backend.Model.Product;
import com.varshneys.ecommerce.ecommerce_backend.Model.User;
import com.varshneys.ecommerce.ecommerce_backend.services.AdminService;


@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    @Autowired
    AdminService adminService;

    // get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    // get all sellers
    @GetMapping("/sellers")
    public List<User> getAllSellers() {
        return adminService.getAllSellers();
    }
    // get all orders
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return adminService.getAllOrders();
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return adminService.getAllProducts();
    }
}

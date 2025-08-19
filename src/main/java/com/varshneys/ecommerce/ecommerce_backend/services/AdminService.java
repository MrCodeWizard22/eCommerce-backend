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

    // User CRUD operations
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // Product CRUD operations
    public Product createProduct(Product product) {
        productService.addProduct(product);
        return product; // Return the product after creation
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            throw new RuntimeException("Product not found");
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());

        productService.updateProduct(existingProduct);
        return existingProduct;
    }

    public void deleteProduct(Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        productService.deleteProductById(id);
    }

    // Order management
    public void updateOrderStatus(Long orderId, String status) {
        orderService.updateOrderStatus(orderId, status);
    }
}

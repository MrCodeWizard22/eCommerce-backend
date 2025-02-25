package com.varshneys.ecommerce.ecommerce_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.varshneys.ecommerce.ecommerce_backend.Model.Product;
import com.varshneys.ecommerce.ecommerce_backend.services.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
// @CrossOrigin(origins = "http://localhost:5173") 
public class ProductController {

    @Autowired
    private ProductService productService;

    // @CrossOrigin(origins = "http://localhost:5173") 
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        System.out.println("Received product: " + product);
        productService.addProduct(product);
        return ResponseEntity.ok().body("{\"message\":\"Product added successfully\"}");
    }
}

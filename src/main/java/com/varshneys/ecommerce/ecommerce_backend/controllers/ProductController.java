package com.varshneys.ecommerce.ecommerce_backend.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.varshneys.ecommerce.ecommerce_backend.Model.Category;
import com.varshneys.ecommerce.ecommerce_backend.Model.Product;
import com.varshneys.ecommerce.ecommerce_backend.Model.Role;
import com.varshneys.ecommerce.ecommerce_backend.Model.User;
import com.varshneys.ecommerce.ecommerce_backend.repository.UserRepository;
import com.varshneys.ecommerce.ecommerce_backend.services.ProductService;

@RestController
@RequestMapping("/api/products")
// @CrossOrigin(origins = "http://localhost:5173") 
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";


    // @CrossOrigin(origins = "http://localhost:5173") 
    @GetMapping
    public List<Product> getAllProducts() {
        // System.out.println("------Inside ProductController.java---------" + productService.getAllProducts().get(0).getImageUrl());
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ResponseEntity<String> addProduct(
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("price") Double price,
        @RequestParam("quantity") Integer quantity,
        @RequestParam(value = "categoryId", required = false) Long categoryId,
        @RequestParam("image") MultipartFile file,
        @RequestParam("sellerId") Long sellerId) {


        try {
            // Save image
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.write(filePath, file.getBytes());

            // Create Product object
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setImageUrl(fileName);
            
            // Set category manually (assuming Product has a Category object)
            Category category = new Category();
            category.setCategoryId(categoryId);
            product.setCategory(category);

            // System.out.println("Seller id before : " + sellerId);
            // for seller 
            User seller = userRepository.findUserById(sellerId);  
            if (seller == null) {
                return ResponseEntity.status(400).body("{\"error\":\"Invalid sellerId\"}");
            }
            
            product.setSeller(seller);


            // Save product
            productService.addProduct(product);

            return ResponseEntity.ok("{\"message\":\"Product added successfully\"}");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("{\"error\":\"Failed to upload image\"}");
        }
       
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/seller/{sellerId}")
    public List<Product> getProductsBySeller(@PathVariable Long sellerId) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (seller.getRole() != Role.SELLER) {
            throw new RuntimeException("User is not a seller");
        }

        return productService.getProductsBySellerId(sellerId);
    }



}

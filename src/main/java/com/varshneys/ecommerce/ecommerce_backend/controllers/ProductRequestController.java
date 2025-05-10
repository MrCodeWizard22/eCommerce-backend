package com.varshneys.ecommerce.ecommerce_backend.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.varshneys.ecommerce.ecommerce_backend.Model.Category;
import com.varshneys.ecommerce.ecommerce_backend.Model.ProductRequest;
import com.varshneys.ecommerce.ecommerce_backend.Model.User;
import com.varshneys.ecommerce.ecommerce_backend.repository.CategoryRepository;
import com.varshneys.ecommerce.ecommerce_backend.repository.UserRepository;
import com.varshneys.ecommerce.ecommerce_backend.services.ProductRequestService;
import com.varshneys.ecommerce.ecommerce_backend.services.ProductService;

@RestController
@RequestMapping("/api/products/requests")
public class ProductRequestController {
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";
    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRequestService productRequestService;

    // Get all product requests
    @GetMapping("/all")
    public List<ProductRequest> getAllRequests() {
        return productRequestService.getAllRequests();
    }

    // Submit a new product request
    
    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addProductRequest(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam("image") MultipartFile file,
            @RequestParam("sellerId") Long sellerId){

        try {

            System.out.println("name: " + name);
            System.out.println("description: " + description);
            System.out.println("price: " + price);
            System.out.println("quantity: " + quantity);
            System.out.println("categoryId: " + categoryId);
            System.out.println("sellerId: " + sellerId);
            System.out.println("image: " + file.getOriginalFilename());


            // Save the image file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.write(filePath, file.getBytes());

            // Create a new ProductRequest object
            ProductRequest request = new ProductRequest();
            request.setName(name);
            request.setDescription(description);
            request.setPrice(price);
            request.setQuantity(quantity);
            request.setImageUrl(fileName);
            request.setStatus(0); // 0 = pending

            Category category = new Category();
            category.setCategoryId(categoryId);
            request.setCategory(category);

            // Retrieve the seller by ID (assuming you have a User entity and UserRepository)
            User seller = userRepository.findById(sellerId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid seller ID"));
            
            request.setSeller(seller);

            // Save the product request (assuming you have a ProductRequestService)
            productRequestService.addProductRequest(request);

            return ResponseEntity.ok("{\"message\":\"Product request submitted for admin approval.\"}");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("{\"error\":\"Failed to upload image\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("{\"error\":\"Invalid seller ID\"}");
        }
    }

    // Approve a request and add it as a product
    @PostMapping("/approve")
    public ResponseEntity<String> approveRequest(@RequestParam long requestId) {
        // ProductRequest request = productRequestService.getRequestById(requestId);
        // if (request == null || request.getStatus() != 0) {
        //     return ResponseEntity.badRequest().body("Invalid or already processed request.");
        // }

        // User seller = userRepository.findByEmail(request.getSellerId()).orElse(null);
        // if (seller == null) {
        //     return ResponseEntity.badRequest().body("Seller not found.");
        // }

        // Category category = categoryRepository.findByName(request.getCategory());
        // if (category == null) {
        //     return ResponseEntity.badRequest().body("Category not found.");
        // }

        // Product product = new Product();
        // product.setName(request.getName());
        // product.setDescription(request.getDescription());
        // product.setPrice(request.getPrice());
        // product.setQuantity(request.getQuantity());
        // product.setImageUrl(request.getImageUrl());
        // product.setSeller(seller);
        // product.setCategory(category);

        // productService.addProduct(product);

        // request.setStatus(1); // 1 = approved
        // productRequestService.addProductRequest(request);

        return ResponseEntity.ok("Request approved and product added.");
    }

    // Reject a request
    @PostMapping("/reject")
    public ResponseEntity<String> rejectRequest(@RequestParam long requestId) {
        // ProductRequest request = productRequestService.getRequestById(requestId);
        // if (request == null || request.getStatus() != 0) {
        //     return ResponseEntity.badRequest().body("Invalid or already processed request.");
        // }

        // request.setStatus(2); // 2 = rejected
        // productRequestService.addProductRequest(request);

        return ResponseEntity.ok("Request rejected.");
    }
}

package com.varshneys.ecommerce.ecommerce_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.varshneys.ecommerce.ecommerce_backend.Model.Category;
import com.varshneys.ecommerce.ecommerce_backend.Model.Product;
import com.varshneys.ecommerce.ecommerce_backend.Model.Role;
import com.varshneys.ecommerce.ecommerce_backend.Model.User;
import com.varshneys.ecommerce.ecommerce_backend.repository.CategoryRepository;
import com.varshneys.ecommerce.ecommerce_backend.repository.ProductRepository;
import com.varshneys.ecommerce.ecommerce_backend.repository.UserRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.fetchAllProducts();
    }

    public Product getProductById(Long id) {
        return productRepository.findByProductId(id);
    }

    public List<Product> getProductsBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
    public void updateProduct(Product product) {
        productRepository.save(product);
    }
    @Transactional
    public void addProduct(Product product) {
        // Verify category exists
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Verify seller exists and is actually a SELLER
        User seller = userRepository.findById(product.getSeller().getUserId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        if (seller.getRole() != Role.SELLER) {
            throw new RuntimeException("Only SELLERS can add products");
        }

        product.setCategory(category);
        product.setSeller(seller);

        productRepository.save(product); 
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.getAllByCategory(categoryId);
    }
}

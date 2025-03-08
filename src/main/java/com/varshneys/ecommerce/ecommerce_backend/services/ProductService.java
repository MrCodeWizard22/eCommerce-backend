package com.varshneys.ecommerce.ecommerce_backend.services;

import com.varshneys.ecommerce.ecommerce_backend.Model.Product;
import com.varshneys.ecommerce.ecommerce_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.fetchAllProducts();
    }

    public Product getProductById(Long id) {
        return productRepository.findByProductId(id);
    }

    @Transactional
    public void addProduct(Product product) {
        productRepository.insertProduct(
            product.getName(), 
            product.getDescription(), 
            product.getPrice(), 
            product.getQuantity(), 
            product.getImageUrl(), 
            product.getCategory().getCategoryId()
        );
    }
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.getAllByCategory(categoryId);
    }
}

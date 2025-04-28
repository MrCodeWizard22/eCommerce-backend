package com.varshneys.ecommerce.ecommerce_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.varshneys.ecommerce.ecommerce_backend.Model.Product;
import com.varshneys.ecommerce.ecommerce_backend.repository.ProductRepository;

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
        productRepository.insertProduct(
            product.getName(), 
            product.getDescription(), 
            product.getPrice(), 
            product.getQuantity(), 
            product.getImageUrl(), 
            product.getCategory().getCategoryId(),
            product.getSeller().getUserId()
        );
    }
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.getAllByCategory(categoryId);
    }
}

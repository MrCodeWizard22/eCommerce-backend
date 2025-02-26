package com.varshneys.ecommerce.ecommerce_backend.services;

import com.varshneys.ecommerce.ecommerce_backend.Model.Product;
import com.varshneys.ecommerce.ecommerce_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void addProduct(Product product) {
        productRepository.insertProduct(product.getName(), product.getDescription(), product.getPrice(), product.getQuantity());
    }
}

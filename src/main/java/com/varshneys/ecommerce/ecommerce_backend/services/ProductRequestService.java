package com.varshneys.ecommerce.ecommerce_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varshneys.ecommerce.ecommerce_backend.Model.ProductRequest;
import com.varshneys.ecommerce.ecommerce_backend.repository.ProductRequestRepository;

@Service
public class ProductRequestService {
    @Autowired
    ProductRequestRepository productRequestRepository;
    
    public List<ProductRequest> getAllRequests() {
        return productRequestRepository.findAllRequests();
    }
    // public List<ProductRequest> getRequestsByStatus(int status) {
    //     return productRequestRepository.findByStatus(status);
    // }
    // public List<ProductRequest> getRequestsBySellerEmail(String sellerEmail) {
    //     return productRequestRepository.findBySellerEmail(sellerEmail);
    // }
    // public List<ProductRequest> getRequestsByCategory(String category) {
    //     return productRequestRepository.findByCategory(category);
    // }

    public void addProductRequest(ProductRequest productRequest) {
        productRequestRepository.addProductRequest( 
            productRequest.getDescription(),
            productRequest.getImageUrl(),
            productRequest.getName(), 
            productRequest.getPrice(),
            productRequest.getQuantity(),
            productRequest.getStatus(),
            productRequest.getCategory().getCategoryId(),
            productRequest.getSeller().getUserId()
        );
    }

    // get request by Id
    // public ProductRequest getRequestById(Long id) {
    //     return productRequestRepository.findById(id).orElse(null);
    // }
}

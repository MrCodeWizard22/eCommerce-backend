package com.varshneys.ecommerce.ecommerce_backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.ProductRequest;

import jakarta.transaction.Transactional;
@Repository
public interface ProductRequestRepository extends CrudRepository<ProductRequest, Long> {

    // request product add 
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO product_requests(description, image_url, name, price, quantity, status, category_id, seller_id) VALUES (:description, :imageUrl, :name, :price, :quantity, :status, :categoryId, :sellerId)", nativeQuery = true)
    void addProductRequest(
        @Param("description") String description, 
        @Param("imageUrl") String imageUrl,
        @Param("name") String name, 
        @Param("price") Double price, 
        @Param("quantity") Integer quantity,
        @Param("status") int status, 
        @Param("categoryId") Long categoryId, 
        @Param("sellerId") Long sellerId
    );

    // find by request id
    // @Query("SELECT p FROM ProductRequest p WHERE p.status = ?1")
    // List<ProductRequest> findByStatus(int status);

    // @Query("SELECT p FROM ProductRequest p WHERE p.sellerEmail = ?1")
    // List<ProductRequest> findBySellerEmail(String sellerEmail);

    // @Query("SELECT p FROM ProductRequest p WHERE p.category = ?1")
    // List<ProductRequest> findByCategory(String category);

    // @Query("SELECT p FROM ProductRequest p WHERE p.sellerEmail = ?1 AND p.status = ?2")
    // List<ProductRequest> findBySellerEmailAndStatus(String sellerEmail, int status);

    // list all product requests
    @Query("SELECT p FROM ProductRequest p")
    List<ProductRequest> findAllRequests();

}

package com.varshneys.ecommerce.ecommerce_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.varshneys.ecommerce.ecommerce_backend.Model.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    // Fetch all products
    @Query(value = "SELECT * FROM products", nativeQuery = true)
    List<Product> fetchAllProducts();

    // find by seller 
    @Query(value = "SELECT * FROM products WHERE seller_id = :sellerId", nativeQuery = true)
    List<Product> findBySellerId(@Param("sellerId") Long sellerId);
    
    // Find by ID
    @Query(value = "SELECT * FROM products WHERE product_id = :id", nativeQuery = true)
    Product findByProductId(@Param("id") Long id);

    // Insert product (use native query with explicit column names)
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO products (name, description, price, quantity, image_url, category_id, seller_id) VALUES (:name, :description, :price, :quantity, :imageUrl, :categoryId, :sellerId)", nativeQuery = true)
    void insertProduct(
        @Param("name") String name, 
        @Param("description") String description, 
        @Param("price") Double price, 
        @Param("quantity") Integer quantity,
        @Param("imageUrl") String imageUrl,
        @Param("categoryId") Long categoryId,
        @Param("sellerId") Long sellerId
    );

    // Get all products by category
    @Query(value = "SELECT * FROM products WHERE category_id = :categoryId", nativeQuery = true)
    List<Product> getAllByCategory(@Param("categoryId") Long categoryId);
}

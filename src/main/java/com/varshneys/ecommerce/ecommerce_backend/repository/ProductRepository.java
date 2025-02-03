package com.varshneys.ecommerce.ecommerce_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Fetch all products
    @Query(value = "SELECT * FROM products", nativeQuery = true)
    List<Product> fetchAllProducts();

    // Find by ID
    @Query(value = "SELECT * FROM products WHERE product_id = :id", nativeQuery = true)
    Product findByProductId(@Param("id") Long id);

    // Insert product (use native query with explicit column names)
    @Query(value = "INSERT INTO products (name, description, price, quantity) VALUES (:name, :description, :price, :quantity)", nativeQuery = true)
    void insertProduct(
        @Param("name") String name, 
        @Param("description") String description, 
        @Param("price") Double price, 
        @Param("quantity") Integer quantity
    );

}

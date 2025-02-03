package com.varshneys.ecommerce.ecommerce_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // fetchall 
    @Query(value = "SELECT * FROM Product", nativeQuery = true)
    List<Product> fetchAllProducts();
    // findbyid  

    @Query(value = "SELECT * FROM Product WHERE product_id = :id", nativeQuery = true)
    Product findByProductId(@Param("id") Long id);
    // insert 

    @Query(value = "INSERT INTO Product (product_name, product_description, product_price, product_quantity, product_image) VALUES (:name, :description, :price, :quantity, :image)", nativeQuery = true)
    void insertProduct(@Param("name") String name, @Param("description") String description, @Param("price") Double price, @Param("quantity") Integer quantity, @Param("image") String image);
}

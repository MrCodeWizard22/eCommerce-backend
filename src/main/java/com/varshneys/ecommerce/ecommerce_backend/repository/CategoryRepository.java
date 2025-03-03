package com.varshneys.ecommerce.ecommerce_backend.repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.Category;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Query(value = "SELECT * FROM categories WHERE Lower(name) = Lower(:name)", nativeQuery = true)
    Category findByName(@Param("name") String name);

    @Query(value = "SELECT COUNT(*) FROM categories", nativeQuery = true)
    long countCategories();

    @Query(value = "SELECT * FROM categories WHERE Lower(name) LIKE Lower(CONCAT('%', :keyword, '%'))", nativeQuery = true)
    List<Category> findByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM categories WHERE id = :id", nativeQuery = true)
    Optional<Category> findById(@Param("id") Long id);

    @Query(value = "SELECT * FROM categories WHERE id >= :id", nativeQuery = true)
    List<Category> findByIdGreaterThan(@Param("id") Long id);

    @Query(value = "SELECT * FROM categories WHERE id < :id", nativeQuery = true)
    List<Category> findByIdLessThanEqual(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM categories WHERE id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);
}

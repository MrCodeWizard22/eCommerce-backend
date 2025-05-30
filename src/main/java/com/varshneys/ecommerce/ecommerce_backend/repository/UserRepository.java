package com.varshneys.ecommerce.ecommerce_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.varshneys.ecommerce.ecommerce_backend.Model.User;


public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email") 
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u.id FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Long getUserIdByEmail(@Param("email") String email);

    // find all 
    @Query("SELECT u FROM User u")
    List<User> findAllUsers();

    // find by id 
    @Query("SELECT u FROM User u WHERE u.userId = :id")
    User findUserById(@Param("id") Long id);
}

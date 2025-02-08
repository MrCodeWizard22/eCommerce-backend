package com.varshneys.ecommerce.ecommerce_backend.repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email") 
    Optional<User> findByEmail(@Param("email") String email);
}

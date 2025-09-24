package com.varshneys.ecommerce.ecommerce_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>{
    
}

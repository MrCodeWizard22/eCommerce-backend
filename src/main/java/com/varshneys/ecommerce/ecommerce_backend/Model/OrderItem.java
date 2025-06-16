package com.varshneys.ecommerce.ecommerce_backend.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderItemId;

    private int quantity;
    private double price;
    private double totalPrice; 
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonBackReference("order-items")
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    // getter and setter methods
    


}

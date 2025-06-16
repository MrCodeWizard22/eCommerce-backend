package com.varshneys.ecommerce.ecommerce_backend.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;
    @Column(nullable = false)
    private String orderDate;
    
    private String shippingAddress;
    private String paymentMethod;
    private double orderTotal;
    private int orderStatus;

    @JsonBackReference("user-orders")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference("order-items")
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    // getter and setter methods
    
}

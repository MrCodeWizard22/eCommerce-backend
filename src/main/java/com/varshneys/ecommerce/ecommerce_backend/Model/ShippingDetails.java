package com.varshneys.ecommerce.ecommerce_backend.Model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "shipping_details")
public class ShippingDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shippingId;
    
    // Shipping Address Details
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String addressLine1;
    
    private String addressLine2;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;
    
    @Column(nullable = false)
    private String pincode;
    
    @Column(nullable = false)
    private String country;
    
    // Shipping Method and Tracking
    private String shippingMethod; // STANDARD, EXPRESS, OVERNIGHT
    private String shippingCarrier; // FEDEX, UPS, DHL, INDIA_POST
    private String trackingNumber;
    private double shippingCost;
    
    // Delivery Estimates
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private LocalDateTime shippedDate;
    
    // Delivery Instructions
    private String deliveryInstructions;
    private String deliveryStatus; 
    
    // Tracking Updates
    private String lastTrackingUpdate;
    private LocalDateTime lastTrackingUpdateTime;
    
    @JsonBackReference("order-shipping")
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}

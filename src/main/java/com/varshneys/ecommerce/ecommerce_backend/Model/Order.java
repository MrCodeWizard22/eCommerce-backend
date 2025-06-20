package com.varshneys.ecommerce.ecommerce_backend.Model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    // Razorpay integration fields
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    // Enhanced order tracking fields
    private String notes;
    private String cancellationReason;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String trackingNumber;
    private String shippingCarrier;
    private double shippingCost;
    private String shippingMethod;

    // Payment tracking
    private LocalDateTime paymentDate;
    private String paymentStatus; // PENDING, COMPLETED, FAILED, REFUNDED
    private String refundId;
    private double refundAmount;
    private LocalDateTime refundDate;

    // Order timeline
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    @JsonBackReference("user-orders")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference("order-items")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShippingDetails shippingDetails;

    // getter and setter methods

}

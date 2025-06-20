package com.varshneys.ecommerce.ecommerce_backend.payload;

import java.time.LocalDateTime;
import java.util.List;

import com.varshneys.ecommerce.ecommerce_backend.Model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String orderDate;
    private String shippingAddress;
    private String paymentMethod;
    private double orderTotal;
    private OrderStatus orderStatus;
    private String paymentStatus;
    private Long userId;
    private String userName;
    private String userEmail;
    private List<OrderItemDTO> orderItems;
    private String razorpayOrderId;
    private String razorpayPaymentId;

    // Enhanced tracking fields
    private String trackingNumber;
    private String shippingCarrier;
    private double shippingCost;
    private String shippingMethod;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private LocalDateTime paymentDate;
    private String notes;
    private String cancellationReason;

    // Timeline tracking
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    // Shipping details
    private ShippingDetailsDTO shippingDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long orderItemId;
        private int quantity;
        private double price;
        private double totalPrice;
        private Long productId;
        private String productName;
        private String productImageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingDetailsDTO {
        private Long shippingId;
        private String fullName;
        private String email;
        private String phone;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String pincode;
        private String country;
        private String deliveryStatus;
        private String deliveryInstructions;
        private String lastTrackingUpdate;
        private LocalDateTime lastTrackingUpdateTime;
    }
}

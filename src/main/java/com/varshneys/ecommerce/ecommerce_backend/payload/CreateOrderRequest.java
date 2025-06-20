package com.varshneys.ecommerce.ecommerce_backend.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private Long userId;
    private String shippingAddress;
    private String paymentMethod;
    private List<OrderItemRequest> items;
    private String notes;

    // Enhanced shipping information
    private ShippingDetailsRequest shippingDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
        private double price; // Price at the time of order
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingDetailsRequest {
        private String fullName;
        private String email;
        private String phone;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String pincode;
        private String country;
        private String shippingMethod;
        private String deliveryInstructions;
    }
}

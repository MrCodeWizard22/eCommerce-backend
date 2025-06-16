package com.varshneys.ecommerce.ecommerce_backend.payload;

import lombok.Data;

@Data
public class PaymentRequest {
    private int amount;
    private Long orderId;
    private String currency = "INR";
    private String notes;
}
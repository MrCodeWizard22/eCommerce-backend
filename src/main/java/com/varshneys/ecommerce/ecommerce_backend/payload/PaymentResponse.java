package com.varshneys.ecommerce.ecommerce_backend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String status;
    private String message;
    private String orderId;
    private String paymentId;
    private String signature;
}
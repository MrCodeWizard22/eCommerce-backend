package com.varshneys.ecommerce.ecommerce_backend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private int amount;
    private String currency;
    private String keyId;
}
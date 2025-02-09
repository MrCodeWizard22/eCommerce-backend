package com.varshneys.ecommerce.ecommerce_backend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
}

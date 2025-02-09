package com.varshneys.ecommerce.ecommerce_backend.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class KeyGenerator {

    public static void main(String[] args) {
        String secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256).toString();
        System.out.println("Generated Secret Key: " + secretKey);
    }
}

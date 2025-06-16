package com.varshneys.ecommerce.ecommerce_backend.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;
    
    @Value("${razorpay.key.secret}")
    private String keySecret;
    
    public Order createRazorpayOrder(int amount) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
        
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_" + System.currentTimeMillis());
        
        return razorpayClient.orders.create(orderRequest);
    }
    
    public boolean validatePaymentSignature(String orderId, String paymentId, String signature) {
        try {
            String data = orderId + "|" + paymentId;
            com.razorpay.Utils.verifySignature(data, keySecret, signature);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

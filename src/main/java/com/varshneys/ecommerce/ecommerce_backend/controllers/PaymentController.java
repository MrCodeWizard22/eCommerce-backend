package com.varshneys.ecommerce.ecommerce_backend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.varshneys.ecommerce.ecommerce_backend.payload.OrderResponse;
import com.varshneys.ecommerce.ecommerce_backend.payload.PaymentRequest;
import com.varshneys.ecommerce.ecommerce_backend.payload.PaymentResponse;
import com.varshneys.ecommerce.ecommerce_backend.services.OrderService;
import com.varshneys.ecommerce.ecommerce_backend.services.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Value("${razorpay.key.id}")
    private String keyId;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequest paymentRequest) {
        try {
            com.razorpay.Order razorpayOrder = paymentService.createRazorpayOrder(paymentRequest.getAmount());
            
            JSONObject orderJson = new JSONObject(razorpayOrder.toString());
            
            OrderResponse response = new OrderResponse(
                orderJson.getString("id"),
                orderJson.getInt("amount"), 
                orderJson.getString("currency"),
                keyId
            );
            
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to create Razorpay order: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> paymentData) {
        try {
            // Get payment data
            String razorpayOrderId = paymentData.get("razorpay_order_id");
            String razorpayPaymentId = paymentData.get("razorpay_payment_id");
            String razorpaySignature = paymentData.get("razorpay_signature");
            Long orderId = paymentData.containsKey("order_id") ? 
                Long.parseLong(paymentData.get("order_id")) : null;
            
            // Verify signature
            boolean isValid = paymentService.validatePaymentSignature(
                razorpayOrderId, razorpayPaymentId, razorpaySignature);
            
            if (isValid) {
                // Update order status if orderId is provided
                if (orderId != null) {
                    orderService.updateOrderStatus(orderId, "PAID");
                }
                
                PaymentResponse response = new PaymentResponse(
                    "success",
                    "Payment verified successfully",
                    razorpayOrderId,
                    razorpayPaymentId,
                    razorpaySignature
                );
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "failed");
                errorResponse.put("message", "Invalid payment signature");
                return ResponseEntity.status(400).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred during payment verification: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

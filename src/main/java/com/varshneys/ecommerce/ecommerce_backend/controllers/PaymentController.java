package com.varshneys.ecommerce.ecommerce_backend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.varshneys.ecommerce.ecommerce_backend.Model.Order;
import com.varshneys.ecommerce.ecommerce_backend.payload.CreateOrderRequest;
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
                    orderService.updatePaymentDetails(orderId, razorpayOrderId, razorpayPaymentId, razorpaySignature);
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

    @PostMapping("/create-order-with-payment")
    public ResponseEntity<?> createOrderWithPayment(@RequestBody CreateOrderRequest orderRequest) {
        try {
            // Create the order first
            Order order = orderService.createOrder(orderRequest);

            // Create Razorpay order for payment
            com.razorpay.Order razorpayOrder = paymentService.createRazorpayOrder((int) order.getOrderTotal());

            JSONObject orderJson = new JSONObject(razorpayOrder.toString());

            // Update order with Razorpay order ID
            order.setRazorpayOrderId(orderJson.getString("id"));
            orderService.updatePaymentDetails(order.getOrderId(), orderJson.getString("id"), null, null);

            OrderResponse response = new OrderResponse(
                orderJson.getString("id"),
                orderJson.getInt("amount"),
                orderJson.getString("currency"),
                keyId
            );

            // Add our order ID to response
            Map<String, Object> fullResponse = new HashMap<>();
            fullResponse.put("razorpayOrder", response);
            fullResponse.put("orderId", order.getOrderId());
            fullResponse.put("orderTotal", order.getOrderTotal());

            return ResponseEntity.ok(fullResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to create order: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/create-order-from-cart")
    public ResponseEntity<?> createOrderFromCart(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            String shippingAddress = request.get("shippingAddress").toString();
            String paymentMethod = request.get("paymentMethod").toString();

            // Create order from cart
            Order order = orderService.createOrderFromCart(userId, shippingAddress, paymentMethod);

            // Calculate total including shipping if applicable
            double totalAmount = order.getOrderTotal();
            if (order.getShippingCost() > 0) {
                totalAmount += order.getShippingCost();
            }

            // Create Razorpay order for payment
            com.razorpay.Order razorpayOrder = paymentService.createRazorpayOrder((int) totalAmount);

            JSONObject orderJson = new JSONObject(razorpayOrder.toString());

            // Update order with Razorpay order ID
            order.setRazorpayOrderId(orderJson.getString("id"));
            orderService.updatePaymentDetails(order.getOrderId(), orderJson.getString("id"), null, null);

            OrderResponse response = new OrderResponse(
                orderJson.getString("id"),
                orderJson.getInt("amount"),
                orderJson.getString("currency"),
                keyId
            );

            // Add our order ID to response
            Map<String, Object> fullResponse = new HashMap<>();
            fullResponse.put("razorpayOrder", response);
            fullResponse.put("orderId", order.getOrderId());
            fullResponse.put("orderTotal", order.getOrderTotal());

            return ResponseEntity.ok(fullResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to create order from cart: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get supported payment methods
     */
    @GetMapping("/methods")
    public ResponseEntity<?> getPaymentMethods() {
        try {
            Map<String, Object> methods = paymentService.getSupportedPaymentMethods();
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch payment methods: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Initiate refund
     */
    @PostMapping("/refund")
    public ResponseEntity<?> initiateRefund(@RequestBody Map<String, Object> request) {
        try {
            String paymentId = request.get("paymentId").toString();
            double amount = Double.parseDouble(request.get("amount").toString());
            String reason = request.getOrDefault("reason", "Customer request").toString();

            Map<String, Object> result = paymentService.initiateRefund(paymentId, amount, reason);

            if ("success".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(400).body(result);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to initiate refund: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Enhanced payment verification with detailed response
     */
    @PostMapping("/verify-enhanced")
    public ResponseEntity<?> verifyPaymentEnhanced(@RequestBody Map<String, String> paymentData) {
        try {
            String razorpayOrderId = paymentData.get("razorpay_order_id");
            String razorpayPaymentId = paymentData.get("razorpay_payment_id");
            String razorpaySignature = paymentData.get("razorpay_signature");
            Long orderId = paymentData.containsKey("order_id") ?
                Long.parseLong(paymentData.get("order_id")) : null;

            // Use enhanced payment verification
            Map<String, Object> result = paymentService.processPaymentVerification(
                razorpayOrderId, razorpayPaymentId, razorpaySignature, orderId);

            if ("success".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(400).body(result);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred during payment verification: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Simple health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Payment service is running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}

package com.varshneys.ecommerce.ecommerce_backend.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.varshneys.ecommerce.ecommerce_backend.Model.ShippingDetails;
import com.varshneys.ecommerce.ecommerce_backend.services.ShippingService;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {
    
    @Autowired
    private ShippingService shippingService;
    
    /**
     * Get available shipping methods
     */
    @GetMapping("/methods")
    public ResponseEntity<?> getShippingMethods() {
        try {
            // Return simple shipping methods
            List<Map<String, Object>> methods = List.of(
                Map.of(
                    "name", "STANDARD",
                    "displayName", "Standard Delivery",
                    "cost", 50.0,
                    "minDeliveryDays", 5,
                    "maxDeliveryDays", 7
                ),
                Map.of(
                    "name", "EXPRESS",
                    "displayName", "Express Delivery",
                    "cost", 100.0,
                    "minDeliveryDays", 2,
                    "maxDeliveryDays", 3
                ),
                Map.of(
                    "name", "FREE",
                    "displayName", "Free Delivery",
                    "cost", 0.0,
                    "minDeliveryDays", 7,
                    "maxDeliveryDays", 10
                )
            );

            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch shipping methods: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Calculate shipping cost
     */
    @PostMapping("/calculate-cost")
    public ResponseEntity<?> calculateShippingCost(@RequestBody Map<String, Object> request) {
        try {
            String shippingMethod = request.get("shippingMethod").toString();
            String pincode = request.get("pincode").toString();
            double orderTotal = Double.parseDouble(request.get("orderTotal").toString());
            
            double cost = shippingService.calculateShippingCost(shippingMethod, pincode, orderTotal);
            LocalDateTime estimatedDelivery = shippingService.estimateDeliveryDate(shippingMethod, pincode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("shippingCost", cost);
            response.put("estimatedDeliveryDate", estimatedDelivery);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to calculate shipping cost: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Track shipment by tracking number
     */
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<?> trackShipment(@PathVariable String trackingNumber) {
        try {
            Optional<ShippingDetails> shippingOpt = shippingService.trackShipment(trackingNumber);
            
            if (shippingOpt.isPresent()) {
                return ResponseEntity.ok(shippingOpt.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Tracking number not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to track shipment: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Get shipping details by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getShippingDetailsByOrderId(@PathVariable Long orderId) {
        try {
            Optional<ShippingDetails> shippingOpt = shippingService.getShippingDetailsByOrderId(orderId);
            
            if (shippingOpt.isPresent()) {
                return ResponseEntity.ok(shippingOpt.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Shipping details not found for order");
                return ResponseEntity.status(404).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch shipping details: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Update shipping status (Admin/Seller only)
     */
    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<?> updateShippingStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            String trackingUpdate = request.getOrDefault("trackingUpdate", "Status updated");
            
            shippingService.updateShippingStatus(orderId, status, trackingUpdate);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Shipping status updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to update shipping status: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Get shipments by status (Admin/Seller only)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getShipmentsByStatus(@PathVariable String status) {
        try {
            List<ShippingDetails> shipments = shippingService.getShipmentsByStatus(status);
            return ResponseEntity.ok(shipments);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch shipments: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Update tracking information
     */
    @PutMapping("/update-tracking")
    public ResponseEntity<?> updateTrackingInfo(@RequestBody Map<String, String> request) {
        try {
            String trackingNumber = request.get("trackingNumber");
            String update = request.get("update");
            String carrier = request.get("carrier");
            
            shippingService.updateTrackingInfo(trackingNumber, update, carrier);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Tracking information updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to update tracking info: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

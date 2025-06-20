package com.varshneys.ecommerce.ecommerce_backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varshneys.ecommerce.ecommerce_backend.Model.Order;
import com.varshneys.ecommerce.ecommerce_backend.Model.ShippingDetails;
import com.varshneys.ecommerce.ecommerce_backend.repository.ShippingDetailsRepository;

@Service
public class ShippingService {
    
    @Autowired
    private ShippingDetailsRepository shippingDetailsRepository;
    
    /**
     * Create shipping details for an order
     */
    public ShippingDetails createShippingDetails(Order order, ShippingDetails shippingInfo) {
        shippingInfo.setOrder(order);

        // Generate tracking number
        String trackingNumber = generateTrackingNumber();
        shippingInfo.setTrackingNumber(trackingNumber);

        // Set initial delivery status
        shippingInfo.setDeliveryStatus("PENDING");

        // Set default delivery estimate and shipping cost
        LocalDateTime estimatedDelivery = LocalDateTime.now().plusDays(5); // Default 5 days
        shippingInfo.setEstimatedDeliveryDate(estimatedDelivery);
        shippingInfo.setShippingCost(50.0); // Default shipping cost

        return shippingDetailsRepository.save(shippingInfo);
    }
    
    /**
     * Update shipping status
     */
    public void updateShippingStatus(Long orderId, String status, String trackingUpdate) {
        Optional<ShippingDetails> shippingOpt = shippingDetailsRepository.findByOrderId(orderId);
        if (shippingOpt.isPresent()) {
            ShippingDetails shipping = shippingOpt.get();
            shipping.setDeliveryStatus(status);
            shipping.setLastTrackingUpdate(trackingUpdate);
            shipping.setLastTrackingUpdateTime(LocalDateTime.now());
            
            // Update specific timestamps based on status
            switch (status) {
                case "SHIPPED":
                    shipping.setShippedDate(LocalDateTime.now());
                    break;
                case "DELIVERED":
                    shipping.setActualDeliveryDate(LocalDateTime.now());
                    break;
            }
            
            shippingDetailsRepository.save(shipping);
        }
    }
    
    /**
     * Get shipping details by order ID
     */
    public Optional<ShippingDetails> getShippingDetailsByOrderId(Long orderId) {
        return shippingDetailsRepository.findByOrderId(orderId);
    }
    
    /**
     * Track shipment by tracking number
     */
    public Optional<ShippingDetails> trackShipment(String trackingNumber) {
        return shippingDetailsRepository.findByTrackingNumber(trackingNumber);
    }
    
    /**
     * Calculate basic shipping cost
     */
    public double calculateShippingCost(String shippingMethod, String pincode, double orderTotal) {
        double baseCost = 50.0; // Default shipping cost

        // Simple shipping method logic
        if ("Express".equalsIgnoreCase(shippingMethod)) {
            baseCost = 100.0;
        } else if ("Standard".equalsIgnoreCase(shippingMethod)) {
            baseCost = 50.0;
        } else if ("Free".equalsIgnoreCase(shippingMethod)) {
            baseCost = 0.0;
        }

        // Free shipping for orders above certain amount
        if (orderTotal >= 500 && "Standard".equalsIgnoreCase(shippingMethod)) {
            return 0.0;
        }

        // Add location-based surcharge for remote areas
        if (isRemoteArea(pincode)) {
            baseCost += 10.0;
        }

        return baseCost;
    }

    /**
     * Estimate delivery date
     */
    public LocalDateTime estimateDeliveryDate(String shippingMethod, String pincode) {
        int deliveryDays = 5; // Default delivery days

        // Simple delivery estimation
        if ("Express".equalsIgnoreCase(shippingMethod)) {
            deliveryDays = 2;
        } else if ("Standard".equalsIgnoreCase(shippingMethod)) {
            deliveryDays = 5;
        } else if ("Free".equalsIgnoreCase(shippingMethod)) {
            deliveryDays = 7;
        }

        // Add extra days for remote areas
        if (isRemoteArea(pincode)) {
            deliveryDays += 2;
        }

        return LocalDateTime.now().plusDays(deliveryDays);
    }
    
    /**
     * Generate unique tracking number
     */
    private String generateTrackingNumber() {
        String prefix = "QS"; // QuantaShop prefix
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(1000);
        return prefix + timestamp + String.format("%03d", random);
    }
    
    /**
     * Check if pincode is in remote area
     */
    private boolean isRemoteArea(String pincode) {
        // Simple logic - in real implementation, this would check against a database
        // Remote areas typically have pincodes starting with certain digits
        return pincode.startsWith("1") || pincode.startsWith("7") || pincode.startsWith("8");
    }
    
    /**
     * Get shipments by delivery status
     */
    public List<ShippingDetails> getShipmentsByStatus(String status) {
        return shippingDetailsRepository.findByDeliveryStatus(status);
    }
    
    /**
     * Update tracking information
     */
    public void updateTrackingInfo(String trackingNumber, String update, String carrier) {
        Optional<ShippingDetails> shippingOpt = shippingDetailsRepository.findByTrackingNumber(trackingNumber);
        if (shippingOpt.isPresent()) {
            ShippingDetails shipping = shippingOpt.get();
            shipping.setLastTrackingUpdate(update);
            shipping.setLastTrackingUpdateTime(LocalDateTime.now());
            if (carrier != null) {
                shipping.setShippingCarrier(carrier);
            }
            shippingDetailsRepository.save(shipping);
        }
    }
}

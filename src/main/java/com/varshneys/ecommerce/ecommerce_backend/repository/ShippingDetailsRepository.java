package com.varshneys.ecommerce.ecommerce_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.varshneys.ecommerce.ecommerce_backend.Model.ShippingDetails;

@Repository
public interface ShippingDetailsRepository extends JpaRepository<ShippingDetails, Long> {
    
    @Query("SELECT sd FROM ShippingDetails sd WHERE sd.order.orderId = :orderId")
    Optional<ShippingDetails> findByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT sd FROM ShippingDetails sd WHERE sd.trackingNumber = :trackingNumber")
    Optional<ShippingDetails> findByTrackingNumber(@Param("trackingNumber") String trackingNumber);
    
    @Query("SELECT sd FROM ShippingDetails sd WHERE sd.deliveryStatus = :status")
    List<ShippingDetails> findByDeliveryStatus(@Param("status") String status);
    
    @Query("SELECT sd FROM ShippingDetails sd WHERE sd.shippingCarrier = :carrier")
    List<ShippingDetails> findByShippingCarrier(@Param("carrier") String carrier);
    
    @Query("SELECT sd FROM ShippingDetails sd WHERE sd.pincode = :pincode")
    List<ShippingDetails> findByPincode(@Param("pincode") String pincode);
}

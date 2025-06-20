package com.varshneys.ecommerce.ecommerce_backend.services;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.varshneys.ecommerce.ecommerce_backend.Model.PaymentStatus;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Autowired
    private OrderService orderService;

    /**
     * Create Razorpay order with enhanced options
     */
    public Order createRazorpayOrder(int amount) throws RazorpayException {
        return createRazorpayOrder(amount, null);
    }

    public Order createRazorpayOrder(int amount, Map<String, Object> options) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Convert to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_" + System.currentTimeMillis());

        // Add payment methods
        JSONObject paymentCapture = new JSONObject();
        paymentCapture.put("payment_capture", 1);
        orderRequest.put("payment_capture", 1);

        // Add notes if provided
        if (options != null && options.containsKey("notes")) {
            orderRequest.put("notes", new JSONObject((Map<?, ?>) options.get("notes")));
        }

        return razorpayClient.orders.create(orderRequest);
    }

    /**
     * Validate payment signature
     */
    public boolean validatePaymentSignature(String orderId, String paymentId, String signature) {
        try {
            String data = orderId + "|" + paymentId;
            com.razorpay.Utils.verifySignature(data, keySecret, signature);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fetch payment details from Razorpay
     */
    public Payment fetchPaymentDetails(String paymentId) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
        return razorpayClient.payments.fetch(paymentId);
    }

    /**
     * Process payment verification and update order
     */
    public Map<String, Object> processPaymentVerification(String razorpayOrderId,
                                                         String razorpayPaymentId,
                                                         String razorpaySignature,
                                                         Long orderId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Validate signature
            boolean isValid = validatePaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

            if (isValid) {
                // Fetch payment details for additional verification
                Payment payment = fetchPaymentDetails(razorpayPaymentId);

                if ("captured".equals(payment.get("status"))) {
                    // Update order with payment details
                    if (orderId != null) {
                        orderService.updatePaymentDetails(orderId, razorpayOrderId, razorpayPaymentId, razorpaySignature);
                        orderService.updatePaymentStatus(orderId, PaymentStatus.COMPLETED.getDescription());
                    }

                    result.put("status", "success");
                    result.put("message", "Payment verified successfully");
                    result.put("paymentDetails", payment.toJson());
                } else {
                    result.put("status", "failed");
                    result.put("message", "Payment not captured");

                    if (orderId != null) {
                        orderService.updatePaymentStatus(orderId, PaymentStatus.FAILED.getDescription());
                    }
                }
            } else {
                result.put("status", "failed");
                result.put("message", "Invalid payment signature");

                if (orderId != null) {
                    orderService.updatePaymentStatus(orderId, PaymentStatus.FAILED.getDescription());
                }
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Payment verification failed: " + e.getMessage());

            if (orderId != null) {
                orderService.updatePaymentStatus(orderId, PaymentStatus.FAILED.getDescription());
            }
        }

        return result;
    }

    /**
     * Initiate refund
     */
    public Map<String, Object> initiateRefund(String paymentId, double amount, String reason) {
        Map<String, Object> result = new HashMap<>();

        try {
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", (int)(amount * 100)); // Convert to paise
            if (reason != null) {
                JSONObject notes = new JSONObject();
                notes.put("reason", reason);
                refundRequest.put("notes", notes);
            }

            // Create refund
            com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);

            result.put("status", "success");
            result.put("message", "Refund initiated successfully");
            result.put("refundId", refund.get("id"));
            result.put("refundAmount", refund.get("amount"));

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Refund failed: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get supported payment methods
     */
    public Map<String, Object> getSupportedPaymentMethods() {
        Map<String, Object> methods = new HashMap<>();

        methods.put("card", Map.of(
            "name", "Credit/Debit Card",
            "description", "Visa, MasterCard, RuPay, American Express",
            "enabled", true
        ));

        methods.put("netbanking", Map.of(
            "name", "Net Banking",
            "description", "All major banks supported",
            "enabled", true
        ));

        methods.put("wallet", Map.of(
            "name", "Digital Wallets",
            "description", "Paytm, PhonePe, Google Pay, etc.",
            "enabled", true
        ));

        methods.put("upi", Map.of(
            "name", "UPI",
            "description", "Pay using UPI ID or QR code",
            "enabled", true
        ));

        methods.put("emi", Map.of(
            "name", "EMI",
            "description", "Easy monthly installments",
            "enabled", true
        ));

        return methods;
    }
}

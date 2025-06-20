package com.varshneys.ecommerce.ecommerce_backend.Model;

public enum PaymentStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded"),
    PARTIALLY_REFUNDED("Partially Refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentStatus fromDescription(String description) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.description.equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid payment status: " + description);
    }
}

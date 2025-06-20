package com.varshneys.ecommerce.ecommerce_backend.Model;

public enum OrderStatus {
    PENDING(0, "Pending"),
    CONFIRMED(1, "Confirmed"),
    PAID(2, "Paid"),
    PROCESSING(3, "Processing"),
    SHIPPED(4, "Shipped"),
    DELIVERED(5, "Delivered"),
    CANCELLED(6, "Cancelled"),
    REFUNDED(7, "Refunded");

    private final int code;
    private final String description;

    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status code: " + code);
    }

    public static OrderStatus fromDescription(String description) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.description.equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status description: " + description);
    }
}

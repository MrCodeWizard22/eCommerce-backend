package com.varshneys.ecommerce.ecommerce_backend.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.varshneys.ecommerce.ecommerce_backend.Model.Order;
import com.varshneys.ecommerce.ecommerce_backend.Model.OrderItem;
import com.varshneys.ecommerce.ecommerce_backend.Model.OrderStatus;
import com.varshneys.ecommerce.ecommerce_backend.Model.PaymentStatus;
import com.varshneys.ecommerce.ecommerce_backend.Model.Product;
import com.varshneys.ecommerce.ecommerce_backend.Model.ShippingDetails;
import com.varshneys.ecommerce.ecommerce_backend.Model.User;
import com.varshneys.ecommerce.ecommerce_backend.payload.CreateOrderRequest;
import com.varshneys.ecommerce.ecommerce_backend.repository.OrderRepository;
import com.varshneys.ecommerce.ecommerce_backend.repository.ProductRepository;
import com.varshneys.ecommerce.ecommerce_backend.repository.UserRepository;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ShippingService shippingService;

    /**
     * Create a new order from cart items or direct order request
     */
    public Order createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderStatus(OrderStatus.PENDING.getCode());
        order.setPaymentStatus(PaymentStatus.PENDING.getDescription());
        order.setNotes(request.getNotes());

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

            // Check stock availability
            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(itemRequest.getPrice() > 0 ? itemRequest.getPrice() : product.getPrice());
            orderItem.setTotalPrice(orderItem.getPrice() * orderItem.getQuantity());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalAmount += orderItem.getTotalPrice();

            // Update product stock
            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        // Add shipping cost to total amount
        double shippingCost = request.getShippingCost();
        double finalTotal = totalAmount + shippingCost;

        order.setOrderItems(orderItems);
        order.setOrderTotal(finalTotal);
        order.setShippingCost(shippingCost); // Set shipping cost on the order

        Order savedOrder = orderRepository.save(order);

        // Create shipping details if provided
        if (request.getShippingDetails() != null) {
            ShippingDetails shippingDetails = new ShippingDetails();
            CreateOrderRequest.ShippingDetailsRequest shippingReq = request.getShippingDetails();

            shippingDetails.setFullName(shippingReq.getFullName());
            shippingDetails.setEmail(shippingReq.getEmail());
            shippingDetails.setPhone(shippingReq.getPhone());
            shippingDetails.setAddressLine1(shippingReq.getAddressLine1());
            shippingDetails.setAddressLine2(shippingReq.getAddressLine2());
            shippingDetails.setCity(shippingReq.getCity());
            shippingDetails.setState(shippingReq.getState());
            shippingDetails.setPincode(shippingReq.getPincode());
            shippingDetails.setCountry(shippingReq.getCountry());
            shippingDetails.setShippingMethod(shippingReq.getShippingMethod());
            shippingDetails.setDeliveryInstructions(shippingReq.getDeliveryInstructions());
            // Note: shippingCost and estimatedDelivery are handled separately in the order total

            shippingService.createShippingDetails(savedOrder, shippingDetails);
        }

        return savedOrder;
    }

    /**
     * Update order status with timeline tracking
     */
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.fromDescription(status);
        } catch (IllegalArgumentException e) {
            // Try to parse as integer code
            try {
                int statusCode = Integer.parseInt(status);
                orderStatus = OrderStatus.fromCode(statusCode);
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Invalid order status: " + status);
            }
        }

        // Update timeline based on status
        LocalDateTime now = LocalDateTime.now();
        switch (orderStatus) {
            case PENDING:
                // No specific action needed for pending
                break;
            case CONFIRMED:
                order.setConfirmedAt(now);
                break;
            case PAID:
                order.setPaymentDate(now);
                order.setPaymentStatus(PaymentStatus.COMPLETED.getDescription());
                break;
            case PROCESSING:
                // Order is being processed
                break;
            case SHIPPED:
                order.setShippedAt(now);
                // Update shipping status
                shippingService.updateShippingStatus(orderId, "SHIPPED", "Order has been shipped");
                break;
            case DELIVERED:
                order.setDeliveredAt(now);
                // Update shipping status
                shippingService.updateShippingStatus(orderId, "DELIVERED", "Order has been delivered");
                break;
            case CANCELLED:
                order.setCancelledAt(now);
                break;
            case REFUNDED:
                order.setRefundDate(now);
                order.setPaymentStatus(PaymentStatus.REFUNDED.getDescription());
                break;
        }

        order.setOrderStatus(orderStatus.getCode());
        orderRepository.save(order);
    }

    /**
     * Update order status by enum
     */
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(status.getCode());
        orderRepository.save(order);
    }

    /**
     * Cancel an order
     */
    public void cancelOrder(Long orderId, String cancellationReason) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = OrderStatus.fromCode(order.getOrderStatus());

        // Check if order can be cancelled
        if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED ||
            currentStatus == OrderStatus.REFUNDED) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + currentStatus.getDescription());
        }

        // Restore product stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED.getCode());
        order.setCancellationReason(cancellationReason);
        orderRepository.save(order);

        // If payment was made, initiate refund process
        if (currentStatus == OrderStatus.PAID || currentStatus == OrderStatus.PROCESSING) {
            processRefund(order);
        }
    }

    /**
     * Process refund for cancelled order
     */
    private void processRefund(Order order) {
        // In a real application, you would integrate with payment gateway for refund
        // For now, we'll just update the status
        order.setOrderStatus(OrderStatus.REFUNDED.getCode());
        orderRepository.save(order);
    }

    /**
     * Get all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAllOrder();
    }

    /**
     * Get orders by user ID
     */
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findAllOrdersByUserId(userId);
    }

    /**
     * Get orders by seller ID
     */
    public List<Order> getOrdersBySellerId(Long sellerId) {
        return orderRepository.findOrdersBySellerId(sellerId);
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Update payment details after successful payment
     */
    public void updatePaymentDetails(Long orderId, String razorpayOrderId,
                               String razorpayPaymentId, String razorpaySignature) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setRazorpayOrderId(razorpayOrderId);
        order.setRazorpayPaymentId(razorpayPaymentId);
        order.setRazorpaySignature(razorpaySignature);

        orderRepository.save(order);
    }


    /**
     * Update payment status
     */
    public void updatePaymentStatus(Long orderId, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentStatus(paymentStatus);

        // Update order status based on payment status
        if (PaymentStatus.COMPLETED.getDescription().equals(paymentStatus)) {
            order.setOrderStatus(OrderStatus.PAID.getCode());
            order.setPaymentDate(LocalDateTime.now());
        } else if (PaymentStatus.FAILED.getDescription().equals(paymentStatus)) {
            order.setOrderStatus(OrderStatus.CANCELLED.getCode());
            order.setCancelledAt(LocalDateTime.now());
        }

        orderRepository.save(order);
    }

    /**
     * Create order from cart
     */
    public Order createOrderFromCart(Long userId, String shippingAddress, String paymentMethod) {
        return createOrderFromCart(userId, shippingAddress, paymentMethod, 0.0, null);
    }

    /**
     * Create order from cart with shipping cost and details
     */
    public Order createOrderFromCart(Long userId, String shippingAddress, String paymentMethod,
                                   double shippingCost, CreateOrderRequest.ShippingDetailsRequest shippingDetails) {
        // Validate user exists
        userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<com.varshneys.ecommerce.ecommerce_backend.Model.Cart> cartItems = cartService.getCartItems(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(userId);
        request.setShippingAddress(shippingAddress);
        request.setPaymentMethod(paymentMethod);
        request.setShippingCost(shippingCost);
        request.setShippingDetails(shippingDetails);

        List<CreateOrderRequest.OrderItemRequest> items = new ArrayList<>();
        for (com.varshneys.ecommerce.ecommerce_backend.Model.Cart cartItem : cartItems) {
            CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest();
            item.setProductId(cartItem.getProduct().getProductId());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice());
            items.add(item);
        }
        request.setItems(items);

        Order order = createOrder(request);

        // Clear cart after successful order creation
        cartService.clearCart(userId);

        return order;
    }
}

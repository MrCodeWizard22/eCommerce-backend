// CartController.java (Spring Boot)
package com.varshneys.ecommerce.ecommerce_backend.controllers;

import com.varshneys.ecommerce.ecommerce_backend.Model.Cart;
import com.varshneys.ecommerce.ecommerce_backend.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody CartRequest cartRequest) {
        try {
            Cart addedCartItem = cartService.addToCart(cartRequest.getUserId(), cartRequest.getProductId(), cartRequest.getQuantity());
            return new ResponseEntity<>(addedCartItem, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Cart>> getCartItems(@RequestParam Long userId) {
        try {
            List<Cart> cartItems = cartService.getCartItems(userId);
            return new ResponseEntity<>(cartItems, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<Cart> updateCartItemQuantity(@PathVariable Long cartId, @RequestBody CartUpdateRequest cartUpdateRequest) {
        try {
            Cart updatedCartItem = cartService.updateCartItemQuantity(cartId, cartUpdateRequest.getQuantity());
            return new ResponseEntity<>(updatedCartItem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<HttpStatus> removeFromCart(@PathVariable Long cartId) {
        try {
            cartService.removeFromCart(cartId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<HttpStatus> clearCart(@RequestParam Long userId) {
        try {
            cartService.clearCart(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
// Request DTOs
    static class CartRequest {
        private Long userId;
        private Long productId;
        private int quantity;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    static class CartUpdateRequest {
        private int quantity;

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}

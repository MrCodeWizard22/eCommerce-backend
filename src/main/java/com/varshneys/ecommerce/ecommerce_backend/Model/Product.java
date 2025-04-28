package com.varshneys.ecommerce.ecommerce_backend.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String imageUrl;
    
    @OneToMany(mappedBy = "product")
    @JsonIgnore  // Prevents infinite recursion
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product")
    @JsonIgnore  // Prevents infinite recursion
    private List<Cart> cartItems;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private User seller;

    public void setSeller(User seller) {
        this.seller = seller;
    }
    
}

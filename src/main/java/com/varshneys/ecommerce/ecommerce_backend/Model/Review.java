package com.varshneys.ecommerce.ecommerce_backend.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "review")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Review {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @JsonBackReference("user-reviews")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    @Column(nullable = false)
    private String reviewDate;


}

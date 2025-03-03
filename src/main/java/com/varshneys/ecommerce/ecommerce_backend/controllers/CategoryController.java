package com.varshneys.ecommerce.ecommerce_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import com.varshneys.ecommerce.ecommerce_backend.Model.Category;
import com.varshneys.ecommerce.ecommerce_backend.services.CategoryService;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Get all categories
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // Get category by ID
    @GetMapping("/{id}")
    public Optional<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // Get category by name
    @GetMapping("/name/{name}")
    public Category getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name);
    }

    // Search categories by keyword
    @GetMapping("/search/{keyword}")
    public List<Category> searchCategories(@PathVariable String keyword) {
        return categoryService.searchCategories(keyword);
    }

    // Get total number of categories
    @GetMapping("/count")
    public long getTotalCategories() {
        return categoryService.getTotalCategories();
    }

    // Get categories with ID greater than a specific value
    @GetMapping("/greaterThan/{id}")
    public List<Category> getCategoriesWithIdGreaterThan(@PathVariable Long id) {
        return categoryService.getCategoriesWithIdGreaterThan(id);
    }

    // Add a new category
    @PostMapping
    public Category addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    // Update category
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    // Delete category
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}

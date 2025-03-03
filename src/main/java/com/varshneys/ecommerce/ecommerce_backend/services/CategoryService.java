package com.varshneys.ecommerce.ecommerce_backend.services;

import com.varshneys.ecommerce.ecommerce_backend.Model.Category;

import java.util.List;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varshneys.ecommerce.ecommerce_backend.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    //read
    ////get by id

    public Optional<Category> getCategoryById(Long id){
        return categoryRepository.findById(id);
    }
    //  by name 
    public Category getCategoryByName(String name){
        return categoryRepository.findByName(name);
    }
    //get all categories
    public List<Category> getAllCategories(){
        return (List<Category>) categoryRepository.findAll();
    }

    // Search categories by keyword
    public List<Category> searchCategories(String keyword) {
        return categoryRepository.findByKeyword(keyword);
    }

    // Count total categories
    public long getTotalCategories() {
        return categoryRepository.countCategories();
    }

    // Get categories with ID greater than a certain number
    public List<Category> getCategoriesWithIdGreaterThan(Long id) {
        return categoryRepository.findByIdGreaterThan(id);
    }

    // getCategories with id less than 
    public List<Category> getCategoriesWithIdLessThan(Long id) {
        return categoryRepository.findByIdLessThanEqual(id);
    }

    // Add a new category
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Update an existing category
    public Category updateCategory(Long id, Category newCategory) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(newCategory.getName());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // Delete category by ID
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    
}

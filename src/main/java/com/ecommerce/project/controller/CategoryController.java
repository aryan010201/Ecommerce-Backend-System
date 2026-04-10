package com.ecommerce.project.controller;


import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/category")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name="pageSize" ,defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name="sortBy",defaultValue=AppConstants.SORT_CATEGORIES_BY,required = false) String sortBy,
            @RequestParam(name="sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder){


        CategoryResponse allCategories = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }

    @PostMapping("/api/admin/category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO  savedCategoryDTO=categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO,HttpStatus.CREATED);
    }
    @DeleteMapping("/api/admin/category/{categoryID}")
    public ResponseEntity< CategoryDTO> deleteCategory(@PathVariable Long categoryID){
        CategoryDTO deletedCategoryDTO=categoryService.deleteCategory(categoryID);
        return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK);

    }
    @PutMapping("/api/admin/category/{categoryId}")
    public ResponseEntity< CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,@PathVariable Long categoryId){
            CategoryDTO savedCategoryDTO=categoryService.updateCategory(categoryDTO,categoryId);
            return new ResponseEntity<>(savedCategoryDTO,HttpStatus.OK);


    }
}

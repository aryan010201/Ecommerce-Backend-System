package com.ecommerce.project.service;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductService {

    ProductDTO addProduct(Long categoryId, @Valid Product product);

    ProductResponse getAllProducts();

    ProductResponse searchByCategory(Long  categoryId);
}

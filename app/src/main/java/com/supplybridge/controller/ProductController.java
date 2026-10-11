package com.supplybridge.controller;

import com.supplybridge.dto.AdjustStockRequest;
import com.supplybridge.dto.ApiResponse;
import com.supplybridge.model.Product;
import com.supplybridge.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.ok("Products catalog retrieved successfully", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Product details retrieved successfully", product));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Product>> adjustProductStock(@PathVariable("id") Long id,
                                                                   @Valid @RequestBody AdjustStockRequest request) {
        Product updated = productService.adjustStock(id, request.getStockChange());
        return ResponseEntity.ok(ApiResponse.ok("Product stock adjusted successfully", updated));
    }
}

package com.supplybridge.service;

import com.supplybridge.exception.BusinessRuleException;
import com.supplybridge.exception.ResourceNotFoundException;
import com.supplybridge.model.Product;
import com.supplybridge.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product adjustStock(Long productId, Integer stockChange) {
        Product product = getProductById(productId);
        int newStock = product.getAvailableStock() + stockChange;
        if (newStock < 0) {
            throw new BusinessRuleException("Cannot adjust stock below zero. Current stock: " + product.getAvailableStock() + ", requested change: " + stockChange);
        }
        product.setAvailableStock(newStock);
        if (newStock == 0) {
            product.setStatus("OUT_OF_STOCK");
        } else if (newStock <= 15) {
            product.setStatus("LOW_STOCK");
        } else {
            product.setStatus("IN_STOCK");
        }
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
}

package com.supplybridge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateOrderRequest {

    @NotNull(message = "customer_id is required")
    private Long customerId;

    @NotNull(message = "product_id is required")
    private Long productId;

    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be greater than zero")
    private Integer quantity;

    private String idempotencyKey;

    public CreateOrderRequest() {}

    public CreateOrderRequest(Long customerId, Long productId, Integer quantity, String idempotencyKey) {
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.idempotencyKey = idempotencyKey;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}

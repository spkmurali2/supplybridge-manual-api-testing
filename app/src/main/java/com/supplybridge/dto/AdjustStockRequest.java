package com.supplybridge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdjustStockRequest {

    @NotNull(message = "stock_change is required")
    private Integer stockChange; // can be positive or negative adjustment

    public AdjustStockRequest() {}

    public AdjustStockRequest(Integer stockChange) {
        this.stockChange = stockChange;
    }

    public Integer getStockChange() { return stockChange; }
    public void setStockChange(Integer stockChange) { this.stockChange = stockChange; }
}

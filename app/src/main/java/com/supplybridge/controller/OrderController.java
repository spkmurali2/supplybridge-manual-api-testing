package com.supplybridge.controller;

import com.supplybridge.dto.ApiResponse;
import com.supplybridge.dto.CreateOrderRequest;
import com.supplybridge.dto.OrderResponse;
import com.supplybridge.dto.UpdateStatusRequest;
import com.supplybridge.model.Customer;
import com.supplybridge.service.CustomerService;
import com.supplybridge.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;

    public OrderController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @Valid @RequestBody CreateOrderRequest request) {

        Customer customer = customerService.authenticateApiKey(apiKey);
        OrderResponse response = orderService.createOrder(customer, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @PathVariable("id") Long id) {

        Customer customer = customerService.authenticateApiKey(apiKey);
        OrderResponse response = orderService.getOrderById(customer, id);
        return ResponseEntity.ok(ApiResponse.ok("Order retrieved successfully", response));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByCustomer(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @PathVariable("customerId") Long customerId) {

        Customer customer = customerService.authenticateApiKey(apiKey);
        List<OrderResponse> orders = orderService.getOrdersByCustomer(customer, customerId);
        return ResponseEntity.ok(ApiResponse.ok("Customer orders retrieved successfully", orders));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @PathVariable("id") Long id) {

        Customer customer = customerService.authenticateApiKey(apiKey);
        OrderResponse response = orderService.cancelOrder(customer, id);
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled successfully and inventory restored", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateStatusRequest request) {

        Customer customer = customerService.authenticateApiKey(apiKey);
        OrderResponse response = orderService.updateOrderStatus(customer, id, request);
        return ResponseEntity.ok(ApiResponse.ok("Order status updated successfully", response));
    }
}

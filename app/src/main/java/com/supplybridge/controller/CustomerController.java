package com.supplybridge.controller;

import com.supplybridge.dto.ApiResponse;
import com.supplybridge.model.Customer;
import com.supplybridge.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.ok("Customers retrieved successfully", customers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable("id") Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer details retrieved successfully", customer));
    }
}

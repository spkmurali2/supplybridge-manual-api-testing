package com.supplybridge.service;

import com.supplybridge.exception.ForbiddenException;
import com.supplybridge.exception.ResourceNotFoundException;
import com.supplybridge.exception.UnauthorizedException;
import com.supplybridge.model.Customer;
import com.supplybridge.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Customer authenticateApiKey(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new UnauthorizedException("API Key header 'X-API-KEY' is missing or blank");
        }
        Customer customer = customerRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new UnauthorizedException("Invalid or unrecognized API Key"));

        if ("SUSPENDED".equalsIgnoreCase(customer.getStatus())) {
            throw new ForbiddenException("Customer account is suspended and unauthorized to perform operations");
        }
        return customer;
    }

    public void validateCustomerAccess(Customer authenticatedCustomer, Long requestedCustomerId) {
        if (!authenticatedCustomer.getId().equals(requestedCustomerId)) {
            throw new ForbiddenException("Access denied: You cannot access or create resources for another customer ID " + requestedCustomerId);
        }
    }
}

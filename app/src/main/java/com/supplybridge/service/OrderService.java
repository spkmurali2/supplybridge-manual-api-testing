package com.supplybridge.service;

import com.supplybridge.dto.CreateOrderRequest;
import com.supplybridge.dto.OrderResponse;
import com.supplybridge.dto.UpdateStatusRequest;
import com.supplybridge.exception.BusinessRuleException;
import com.supplybridge.exception.ForbiddenException;
import com.supplybridge.exception.ResourceNotFoundException;
import com.supplybridge.model.Customer;
import com.supplybridge.model.Order;
import com.supplybridge.model.Product;
import com.supplybridge.repository.CustomerRepository;
import com.supplybridge.repository.OrderRepository;
import com.supplybridge.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse createOrder(Customer authenticatedCustomer, CreateOrderRequest request) {
        // 1. Authorisation check: customer can only place orders for themselves
        if (!authenticatedCustomer.getId().equals(request.getCustomerId())) {
            throw new ForbiddenException("Forbidden: Authenticated customer " + authenticatedCustomer.getId()
                    + " cannot place an order on behalf of customer " + request.getCustomerId());
        }

        // 2. Check Idempotency Key if provided (prevents duplicate submissions)
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().trim().isEmpty()) {
            Optional<Order> existing = orderRepository.findByIdempotencyKey(request.getIdempotencyKey().trim());
            if (existing.isPresent()) {
                // Return existing order idempotent response
                return OrderResponse.from(existing.get());
            }
        }

        // 3. Customer validation
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        if ("SUSPENDED".equalsIgnoreCase(customer.getStatus())) {
            throw new BusinessRuleException("Customer account is suspended and cannot place new orders.");
        }

        // 4. Quantity validation
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BusinessRuleException("Order quantity must be a positive integer greater than zero.");
        }

        // 5. Product validation
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // 6. Inventory check (Business Rule REQ-003)
        if (product.getAvailableStock() < request.getQuantity()) {
            throw new BusinessRuleException("Insufficient inventory. Requested: " + request.getQuantity()
                    + ", available stock: " + product.getAvailableStock());
        }

        // 7. Calculate total amount (Unit price * quantity, RoundingMode.HALF_UP, 2 decimal places)
        BigDecimal unitPrice = product.getUnitPrice().setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        // 8. Deduct inventory & update product status
        int newStock = product.getAvailableStock() - request.getQuantity();
        product.setAvailableStock(newStock);
        if (newStock == 0) {
            product.setStatus("OUT_OF_STOCK");
        } else if (newStock <= 15) {
            product.setStatus("LOW_STOCK");
        }
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        // 9. Generate unique order number (e.g., ORD-2026-UUID8)
        String orderNumber = "ORD-2026-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 10. Persist order
        Order order = new Order(
                orderNumber,
                customer,
                product,
                request.getQuantity(),
                unitPrice,
                totalAmount,
                "CONFIRMED",
                request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank() ? request.getIdempotencyKey().trim() : null
        );

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    public OrderResponse getOrderById(Customer authenticatedCustomer, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Authorisation: customer can only view their own orders
        if (!authenticatedCustomer.getId().equals(order.getCustomer().getId())) {
            throw new ForbiddenException("Access denied: You do not have permission to view order #" + orderId);
        }

        return OrderResponse.from(order);
    }

    public List<OrderResponse> getOrdersByCustomer(Customer authenticatedCustomer, Long customerId) {
        if (!authenticatedCustomer.getId().equals(customerId)) {
            throw new ForbiddenException("Access denied: You cannot view orders of customer #" + customerId);
        }
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse cancelOrder(Customer authenticatedCustomer, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Authorisation check
        if (!authenticatedCustomer.getId().equals(order.getCustomer().getId())) {
            throw new ForbiddenException("Access denied: You cannot cancel order #" + orderId);
        }

        // Business rule: only PENDING or CONFIRMED orders can be cancelled
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new BusinessRuleException("Order #" + orderId + " is already cancelled.");
        }
        if ("COMPLETED".equalsIgnoreCase(order.getStatus())) {
            throw new BusinessRuleException("Order #" + orderId + " is already completed and cannot be cancelled.");
        }

        // Restore inventory to product
        Product product = order.getProduct();
        int restoredStock = product.getAvailableStock() + order.getQuantity();
        product.setAvailableStock(restoredStock);
        if (restoredStock > 15) {
            product.setStatus("IN_STOCK");
        } else if (restoredStock > 0) {
            product.setStatus("LOW_STOCK");
        }
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        // Update order status
        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);

        return OrderResponse.from(updated);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Customer authenticatedCustomer, Long orderId, UpdateStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!authenticatedCustomer.getId().equals(order.getCustomer().getId())) {
            throw new ForbiddenException("Access denied for order #" + orderId);
        }

        String targetStatus = request.getStatus().toUpperCase();
        String currentStatus = order.getStatus().toUpperCase();

        // Validate allowed state transitions
        // CONFIRMED -> COMPLETED or CANCELLED
        // PENDING -> CONFIRMED or CANCELLED
        if (currentStatus.equals("CANCELLED")) {
            throw new BusinessRuleException("Cannot transition order from CANCELLED to " + targetStatus);
        }
        if (currentStatus.equals("COMPLETED")) {
            throw new BusinessRuleException("Cannot transition order from COMPLETED to " + targetStatus);
        }

        if (targetStatus.equals("CANCELLED")) {
            return cancelOrder(authenticatedCustomer, orderId);
        } else if (targetStatus.equals("COMPLETED") || targetStatus.equals("CONFIRMED")) {
            order.setStatus(targetStatus);
            order.setUpdatedAt(LocalDateTime.now());
            return OrderResponse.from(orderRepository.save(order));
        } else {
            throw new BusinessRuleException("Invalid status transition to: " + targetStatus);
        }
    }
}

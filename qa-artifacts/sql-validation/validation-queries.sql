-- ==============================================================================
-- SupplyBridge SQL Validation Suite
-- Database Quality Assurance Queries for Orders & Inventory Verification
-- Target: PostgreSQL 14+ / psql / pgAdmin / H2 Console
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- 1. BASELINE CATALOG & CUSTOMER VERIFICATION
-- ------------------------------------------------------------------------------

-- Query 1.1: Verify registered customers and account statuses
SELECT id, company_name, contact_name, email, status, created_at 
FROM customers 
ORDER BY id ASC;

-- Query 1.2: Check current warehouse inventory and product statuses
SELECT id, sku, name, unit_price, available_stock, status, updated_at 
FROM products 
ORDER BY id ASC;

-- ------------------------------------------------------------------------------
-- 2. ORDER PERSISTENCE & ACCURACY VALIDATION (After POST /api/v1/orders)
-- ------------------------------------------------------------------------------

-- Query 2.1: Retrieve newly created order record by order number
-- Replace 'ORD-2026-XXXXXXXX' with returned orderNumber
SELECT 
    o.id AS order_id,
    o.order_number,
    o.customer_id,
    c.company_name,
    o.product_id,
    p.name AS product_name,
    o.quantity,
    o.unit_price,
    o.total_amount,
    o.status,
    o.idempotency_key,
    o.created_at
FROM orders o
JOIN customers c ON o.customer_id = c.id
JOIN products p ON o.product_id = p.id
WHERE o.order_number = 'ORD-2026-0001'
ORDER BY o.id DESC;

-- Query 2.2: Validate total amount calculation matches unit price * quantity
-- Expectation: Zero rows returned (all rows must satisfy total_amount = unit_price * quantity)
SELECT 
    id, 
    order_number, 
    quantity, 
    unit_price, 
    total_amount, 
    (quantity * unit_price) AS calculated_expected,
    (total_amount - (quantity * unit_price)) AS discrepancy
FROM orders
WHERE total_amount != (quantity * unit_price);

-- ------------------------------------------------------------------------------
-- 3. INVENTORY DEDUCTION & RESTORATION VALIDATION
-- ------------------------------------------------------------------------------

-- Query 3.1: Check inventory reduction after order creation
-- If product 2 had 120 units and 10 units were ordered, available_stock must be 110
SELECT id, sku, name, available_stock, status 
FROM products 
WHERE id = 2;

-- Query 3.2: Verify stock restoration after order cancellation
-- When order is CANCELLED, available_stock must equal original stock level
SELECT 
    o.id AS order_id,
    o.order_number,
    o.status AS order_status,
    p.id AS product_id,
    p.sku,
    p.available_stock
FROM orders o
JOIN products p ON o.product_id = p.id
WHERE o.id = 100;

-- ------------------------------------------------------------------------------
-- 4. SIDE-EFFECT INTEGRITY: ABSENCE OF PHANTOM RECORDS AFTER REJECTION
-- ------------------------------------------------------------------------------

-- Query 4.1: Confirm zero order records created for invalid/rejected attempts
-- Ensure no phantom order was persisted for customer 4 (suspended) or invalid quantities
SELECT COUNT(*) AS total_orders_for_suspended_customer
FROM orders
WHERE customer_id = 4;

-- Query 4.2: Verify stock never dropped below zero (Sanity Check)
SELECT id, sku, name, available_stock
FROM products
WHERE available_stock < 0;

-- ------------------------------------------------------------------------------
-- 5. IDEMPOTENCY KEY INTEGRITY
-- ------------------------------------------------------------------------------

-- Query 5.1: Verify duplicate requests did not generate duplicate rows
SELECT 
    idempotency_key, 
    COUNT(*) AS row_count
FROM orders
WHERE idempotency_key IS NOT NULL
GROUP BY idempotency_key
HAVING COUNT(*) > 1;
-- Expectation: Zero rows returned (All idempotency keys must be unique in DB)

-- SupplyBridge Database Schema
-- Target: PostgreSQL 14+ / 17 (and ANSI SQL compliant)

-- Drop existing tables safely in reverse order of foreign keys
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS customers CASCADE;

DROP SEQUENCE IF EXISTS customers_id_seq;
DROP SEQUENCE IF EXISTS products_id_seq;
DROP SEQUENCE IF EXISTS orders_id_seq;

-- Sequences for primary keys
CREATE SEQUENCE customers_id_seq START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE products_id_seq START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE orders_id_seq START WITH 100 INCREMENT BY 1;

-- 1. Customers Table
CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    company_name VARCHAR(150) NOT NULL,
    contact_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    api_key VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Products Table
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    available_stock INTEGER NOT NULL CHECK (available_stock >= 0),
    status VARCHAR(30) NOT NULL DEFAULT 'IN_STOCK',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Orders Table
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    order_number VARCHAR(60) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    total_amount NUMERIC(12, 2) NOT NULL CHECK (total_amount >= 0),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    idempotency_key VARCHAR(100) UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance & query lookups
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_product_id ON orders(product_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_customers_api_key ON customers(api_key);

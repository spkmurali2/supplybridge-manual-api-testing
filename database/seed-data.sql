-- SupplyBridge Seed Data Script
-- Fictional B2B Customers, Products, Inventory levels and baseline Orders

-- 1. Insert Sample B2B Customers
INSERT INTO customers (id, company_name, contact_name, email, api_key, status)
VALUES 
    (1, 'Apex Logistics Ltd', 'Sarah Jenkins', 'orders@apexlogistics.co.uk', 'sb_live_cust_apex_7781', 'ACTIVE'),
    (2, 'Nexus Retailers Inc', 'Marcus Vance', 'purchasing@nexusretail.com', 'sb_live_cust_nexus_9924', 'ACTIVE'),
    (3, 'Vanguard Industrial Supply', 'Elena Rostov', 'elena@vanguardindustrial.com', 'sb_live_cust_vanguard_3310', 'ACTIVE'),
    (4, 'Dormant Enterprise Corp', 'Thomas Wright', 'admin@dormantcorp.com', 'sb_live_cust_dormant_0041', 'SUSPENDED');

-- Reset customer sequence to max id
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));

-- 2. Insert Sample B2B Products
INSERT INTO products (id, sku, name, description, unit_price, available_stock, status)
VALUES
    (1, 'IND-PAL-001', 'Heavy Duty Euro Pallet (Grade A)', 'Standard Euro pallet, treated wood, dynamic load 1500kg', 24.50, 500, 'IN_STOCK'),
    (2, 'IND-STR-002', 'Industrial Stretch Film (23 Micron)', 'Hand wrap clear stretch film roll, 500mm x 300m', 14.75, 120, 'IN_STOCK'),
    (3, 'IND-STP-003', 'Steel Strapping Coil 19mm', 'Heavy duty ribbon wound steel strapping for pallet stability', 65.00, 15, 'LOW_STOCK'),
    (4, 'IND-LBL-004', 'Direct Thermal Shipping Labels (4x6)', 'Perforated roll of 1000 labels for thermal barcode printers', 12.00, 0, 'OUT_OF_STOCK'),
    (5, 'IND-CRF-005', 'Double-Wall Corrugated Cartons', 'Pack of 25 heavy duty shipping boxes (600x400x400mm)', 48.00, 80, 'IN_STOCK');

-- Reset product sequence to max id
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));

-- 3. Insert Baseline Historical Orders
INSERT INTO orders (id, order_number, customer_id, product_id, quantity, unit_price, total_amount, status, idempotency_key, created_at)
VALUES
    (1, 'ORD-2026-0001', 1, 1, 10, 24.50, 245.00, 'COMPLETED', 'idem-seed-001', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    (2, 'ORD-2026-0002', 1, 2, 5, 14.75, 73.75, 'CONFIRMED', 'idem-seed-002', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    (3, 'ORD-2026-0003', 2, 3, 2, 65.00, 130.00, 'PENDING', 'idem-seed-003', CURRENT_TIMESTAMP - INTERVAL '1 hour');

-- Reset order sequence to max id
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));

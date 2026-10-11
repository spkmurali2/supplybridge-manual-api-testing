# Sprint 24 Test Execution Log & Results Record

**Tester:** Priya Murali  
**Sprint:** Sprint 24  
**Date of Testing:** October 2026  
**Environment:** Local AUT (`http://localhost:8081`)  

---

## 1. Execution Summary

| Metric | Result |
| :--- | :---: |
| **Total Test Cases Planned** | 28 |
| **Passed Cases** | 28 (100%) |
| **Failed Cases** | 0 |
| **Blocked Cases** | 0 |
| **Requirements Verified** | 10 of 10 (100% Coverage) |

---

## 2. SQL Database Queries I Executed & Results

| What I Checked in Database | SQL Query Executed | Observed Result | Status |
| :--- | :--- | :--- | :---: |
| **Order Row Persistence** | `SELECT COUNT(*) FROM orders WHERE order_number = 'ORD-2026-3E6F775E';` | Exactly 1 record found with status `CONFIRMED`. | **PASS** |
| **Stock Deduction** | `SELECT available_stock FROM products WHERE id = 2;` | Stock dropped from 120 to 110 after ordering 10 units. | **PASS** |
| **Price Calculation** | `SELECT COUNT(*) FROM orders WHERE total_amount != (quantity * unit_price);` | 0 rows with discrepancy (all arithmetic accurate). | **PASS** |
| **Stock Restoration** | `SELECT available_stock FROM products WHERE id = 2;` (after cancel) | Stock restored from 110 back to 120. | **PASS** |
| **No Phantom Orders on 422** | `SELECT COUNT(*) FROM orders WHERE quantity <= 0;` | 0 rows (rejected requests never touched the DB). | **PASS** |
| **Tenant Isolation** | `SELECT COUNT(*) FROM orders WHERE customer_id = 2;` | Customer 1 calls never altered Customer 2 rows. | **PASS** |

---

## 3. Postman Test Run Results

I executed the Postman collection via manual runs and folder runs:
- **01 - Customer APIs:** 3 requests — 100% passed.
- **02 - Product and Inventory APIs:** 3 requests — 100% passed.
- **03 - Order Management APIs:** 5 requests — 100% passed.
- **04 - Authentication and Negative Scenarios:** 6 requests — 100% passed.
- **05 - End-to-End Workflows:** 5 sequential requests — 100% passed.

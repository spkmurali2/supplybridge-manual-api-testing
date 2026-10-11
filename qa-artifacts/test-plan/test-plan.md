# Sprint QA Test Plan

**Project:** SupplyBridge B2B API  
**Sprint:** Sprint 24  
**QA Tester:** Priya Murali  

---

## 1. Objective

My goal for this test plan was to verify that the SupplyBridge order management API correctly processes B2B orders, prevents inventory overselling, maintains tenant isolation, and accurately updates database records.

---

## 2. Testing Scope

### In Scope
- **Customer Endpoints:** `GET /api/v1/customers`, `GET /api/v1/customers/{id}`
- **Product Endpoints:** `GET /api/v1/products`, `GET /api/v1/products/{id}`, `PATCH /api/v1/products/{id}/stock`
- **Order Endpoints:**
  - Create Order (`POST /api/v1/orders`)
  - Retrieve Order (`GET /api/v1/orders/{id}`)
  - List Customer Orders (`GET /api/v1/orders/customer/{customerId}`)
  - Cancel Order (`POST /api/v1/orders/{id}/cancel`)
  - Status Transitions (`PATCH /api/v1/orders/{id}/status`)
- **Database Consistency (SQL):** Checking `orders`, `products`, and `customers` tables before and after API operations.
- **Security & Authorization:** Verifying `X-API-KEY` enforcement and blocking cross-customer access.

### Out of Scope (For this manual testing project)
- UI / Frontend testing (the AUT is a headless REST API).
- Java automation frameworks (REST Assured / TestNG) — planned for a separate follow-up repository.
- Performance load testing.

---

## 3. My Testing Strategy

1. **Pre-condition Setup:** Start the local API server and confirm database tables and seed data are ready.
2. **Postman Manual Execution:** Send requests using the configured Postman environment variables.
3. **API Response Check:** Inspect the HTTP status code, response time, and JSON response body.
4. **Direct Database Check:** Run SQL queries to ensure the database accurately reflects the changes (e.g., verifying stock dropped or row was inserted).
5. **Defect Tracking:** If a behavior violates an acceptance criterion, document it with clear reproduction steps and database evidence.

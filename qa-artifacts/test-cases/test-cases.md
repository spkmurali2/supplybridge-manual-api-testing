# Manual Test Cases & Traceability Matrix

**QA Tester:** Priya Murali  
**Sprint:** Sprint 24  
**Total Test Cases:** 28  

---

## 1. Traceability Matrix

Here is how my test cases trace back to each business requirement:

| Requirement ID | Description | Test Cases |
| :--- | :--- | :--- |
| **REQ-001** | Customer Existence & Status Check | TC-003, TC-016 |
| **REQ-002** | Product Existence Check | TC-004 |
| **REQ-003** | Inventory Availability Limit | TC-008, TC-009, TC-019 |
| **REQ-004** | Inventory Deduction on Success | TC-001, TC-017, TC-026 |
| **REQ-005** | Order Total Calculation & Database Persistence | TC-001, TC-005, TC-006, TC-007, TC-018, TC-020 |
| **REQ-006** | Order Retrieval by ID & List | TC-002, TC-025 |
| **REQ-007** | Order Cancellation & Inventory Restoration | TC-013, TC-022, TC-027 |
| **REQ-008** | Order Lifecycle State Transitions | TC-015, TC-023 |
| **REQ-009** | Authentication & Tenant Isolation | TC-010, TC-011, TC-012, TC-024 |
| **REQ-010** | Duplicate Prevention & Idempotency Key | TC-014, TC-021, TC-028 |

---

## 2. Key Executed Test Cases

### TC-001: Create Order with Valid Data (Happy Path)
- **Requirement:** REQ-004, REQ-005
- **Priority:** High
- **Method & Endpoint:** `POST /api/v1/orders`
- **Headers:** `X-API-KEY: sb_live_cust_apex_7781`, `Content-Type: application/json`
- **Request Body:** `{ "customerId": 1, "productId": 2, "quantity": 10 }`
- **Steps I Executed:**
  1. Ran `SELECT available_stock FROM products WHERE id = 2;` (initial stock: 120).
  2. Sent POST request via Postman.
  3. Checked HTTP status code and response payload.
  4. Ran SQL query to verify order row was created and stock reduced.
- **Expected Result:** Status `201 Created`, total amount `147.50` (`14.75 * 10`), stock reduced to 110.
- **Actual Result:** Passed. Order created with number `ORD-2026-3E6F775E`. Stock verified at 110 in SQL.
- **Status:** **PASS**

---

### TC-009: Reject Order Exceeding Warehouse Stock (Boundary Negative)
- **Requirement:** REQ-003
- **Priority:** Critical
- **Method & Endpoint:** `POST /api/v1/orders`
- **Request Body:** `{ "customerId": 1, "productId": 2, "quantity": 99999 }`
- **Steps I Executed:** Sent order requesting far more units than exist in the warehouse.
- **Expected Result:** Status `422 Unprocessable Entity`, message `BUSINESS_RULE_VIOLATION`. Database stock unchanged.
- **Actual Result:** Passed. Returned `422` with `"Insufficient inventory. Requested: 99999, available stock: 110"`. SQL check confirmed stock stayed at 110.
- **Status:** **PASS**

---

### TC-011: Attempt Accessing Another Customer's Order (Security Authorization)
- **Requirement:** REQ-009
- **Priority:** Critical
- **Method & Endpoint:** `GET /api/v1/orders/3`
- **Headers:** `X-API-KEY: sb_live_cust_apex_7781` (Customer 1's key)
- **Steps I Executed:** As Customer 1, tried retrieving Order 3 (which belongs to Customer 2).
- **Expected Result:** Status `403 Forbidden`. No order details returned.
- **Actual Result:** Passed. Returned `403 Forbidden` with `"Access denied: You do not have permission to view order #3"`.
- **Status:** **PASS**

---

### TC-013: Cancel Order and Restore Warehouse Inventory (Lifecycle)
- **Requirement:** REQ-007
- **Priority:** High
- **Method & Endpoint:** `POST /api/v1/orders/100/cancel`
- **Steps I Executed:**
  1. Cancelled the order created in TC-001.
  2. Queried database: `SELECT available_stock FROM products WHERE id = 2;`.
- **Expected Result:** Status `200 OK`, order status changed to `CANCELLED`, stock returned from 110 to 120.
- **Actual Result:** Passed. Status set to `CANCELLED` and stock verified at 120 in SQL.
- **Status:** **PASS**

---

### TC-014: Duplicate Order Submission with Idempotency Key
- **Requirement:** REQ-010
- **Priority:** High
- **Method & Endpoint:** `POST /api/v1/orders`
- **Request Body:** `{ "customerId": 1, "productId": 1, "quantity": 2, "idempotencyKey": "unique-idem-014" }`
- **Steps I Executed:** Sent the exact same request twice back to back with the same idempotency key.
- **Expected Result:** Second request returns existing order without creating a duplicate record or deducting stock a second time.
- **Actual Result:** Passed. Second request returned original order number. Database count showed exactly 1 order row.
- **Status:** **PASS**

---

## 3. Summary of All 28 Test Cases Executed

| TC ID | Test Scenario | Requirement | Method & Endpoint | Expected Status | Result |
| :--- | :--- | :--- | :--- | :---: | :---: |
| **TC-001** | Create order with valid data (Happy path) | REQ-004, 005 | `POST /orders` | 201 | **PASS** |
| **TC-002** | Retrieve created order by ID | REQ-006 | `GET /orders/1` | 200 | **PASS** |
| **TC-003** | Order with non-existent customer ID | REQ-001 | `POST /orders` | 403 / 404 | **PASS** |
| **TC-004** | Order with non-existent product ID | REQ-002 | `POST /orders` | 404 | **PASS** |
| **TC-005** | Order with missing required field (`productId`) | REQ-005 | `POST /orders` | 400 | **PASS** |
| **TC-006** | Malformed JSON request body | REQ-005 | `POST /orders` | 400 | **PASS** |
| **TC-007** | Order with zero or negative quantity | REQ-005 | `POST /orders` | 400 | **PASS** |
| **TC-008** | Order exactly the remaining available stock | REQ-003, 004 | `POST /orders` | 201 | **PASS** |
| **TC-009** | Order exceeding available warehouse stock | REQ-003 | `POST /orders` | 422 | **PASS** |
| **TC-010** | Request without `X-API-KEY` header | REQ-009 | `POST /orders` | 401 | **PASS** |
| **TC-011** | Customer 1 viewing Customer 2's order (BOLA check) | REQ-009 | `GET /orders/3` | 403 | **PASS** |
| **TC-012** | Customer 1 placing order under Customer 2's ID | REQ-009 | `POST /orders` | 403 | **PASS** |
| **TC-013** | Cancel confirmed order & verify stock restored | REQ-007 | `POST /orders/{id}/cancel` | 200 | **PASS** |
| **TC-014** | Duplicate request submission with idempotency key | REQ-010 | `POST /orders` | 200 / 201 | **PASS** |
| **TC-015** | Attempt status change on already cancelled order | REQ-008 | `PATCH /orders/{id}/status` | 422 | **PASS** |
| **TC-016** | Order attempt by suspended customer account | REQ-001 | `POST /orders` | 403 / 422 | **PASS** |
| **TC-017** | Verify stock deduction across multiple items | REQ-004 | `POST /orders` | 201 | **PASS** |
| **TC-018** | Unit price snapshot remains fixed on order row | REQ-005 | `POST /orders` | 201 | **PASS** |
| **TC-019** | Off-by-one boundary: order `available_stock + 1` | REQ-003 | `POST /orders` | 422 | **PASS** |
| **TC-020** | Invalid quantity type (string `"abc"`) | REQ-005 | `POST /orders` | 400 | **PASS** |
| **TC-021** | Order creation without idempotency key | REQ-010 | `POST /orders` | 201 | **PASS** |
| **TC-022** | Attempt to cancel already COMPLETED order | REQ-007 | `POST /orders/1/cancel` | 422 | **PASS** |
| **TC-023** | Attempt to cancel already CANCELLED order | REQ-007 | `POST /orders/{id}/cancel` | 422 | **PASS** |
| **TC-024** | Customer querying orders of another customer account | REQ-009 | `GET /orders/customer/2` | 403 | **PASS** |
| **TC-025** | Customer querying their own order history | REQ-006 | `GET /orders/customer/1` | 200 | **PASS** |
| **TC-026** | Verify catalog stock reflects real-time updates | REQ-004 | `GET /products` | 200 | **PASS** |
| **TC-027** | Warehouse restocking adjustment via stock endpoint | REQ-004 | `PATCH /products/{id}/stock` | 200 | **PASS** |
| **TC-028** | Full End-to-End procurement workflow | REQ-001..010 | Full Flow | 200 / 201 | **PASS** |

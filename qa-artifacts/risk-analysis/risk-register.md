# Risk Register & Prioritisation Matrix

**Author / QA Tester:** Priya Murali  
**Sprint:** Sprint 24  

---

## 1. Why I Performed Risk-Based Testing

Instead of testing random endpoints, I identified the highest business risks first. In a B2B ordering system, financial miscalculations, data leaks, and inventory overselling have serious legal and financial consequences.

I ranked risks using a simple formula:  
**Risk Score = Likelihood (1-3) × Impact (1-3)**

---

## 2. Identified Risks & My Testing Approach

| Risk ID | Risk Description | Likelihood | Impact | Score | How I Tested & Verified It | Test Cases |
| :--- | :--- | :---: | :---: | :---: | :--- | :--- |
| **RSK-001** | **Inventory Overselling:** Customers order more than available stock, causing unfulfillable orders and negative inventory in warehouse. | High (3) | Critical (3) | **9 (High)** | Boundary testing (`stock + 1` unit). Verified API returns `422` and database `available_stock` never drops below 0. | TC-008, TC-009 |
| **RSK-002** | **Cross-Tenant Data Breach:** Customer A accesses Customer B's order by tampering with the URL ID (IDOR / BOLA vulnerability). | Med (2) | Critical (3) | **6 (High)** | Tested with Customer 1's API key requesting Customer 2's order ID. Verified response is `403 Forbidden` and no data is leaked. | TC-011, TC-012, TC-024 |
| **RSK-003** | **Financial Calculation Bug:** `totalAmount` calculated incorrectly due to floating point math or client tampering. | Med (2) | Critical (3) | **6 (High)** | Tested ordering multiple quantities and verified `total_amount = unit_price * quantity` exactly matches in SQL. | TC-001, TC-018 |
| **RSK-004** | **Phantom Database Side Effects:** A rejected request (e.g., negative quantity or 422 error) still accidentally deducts stock. | Med (2) | Major (2) | **4 (Med)** | Queried database before and after rejected requests to confirm zero stock changes or orphan rows. | TC-007, TC-019, TC-020 |
| **RSK-005** | **Double Charging / Duplicate Orders:** Network retries cause the same order to be created twice. | High (3) | Major (2) | **6 (High)** | Sent duplicate requests using the same `idempotencyKey`. Confirmed only 1 order row created in SQL and stock reduced only once. | TC-014, TC-021 |
| **RSK-006** | **Inventory Lost on Cancellation:** An order is cancelled but warehouse stock is not restored. | Low (1) | Major (2) | **2 (Med)** | Cancelled a confirmed order and verified in SQL that `products.available_stock` increased by the exact quantity. | TC-013, TC-022 |
| **RSK-007** | **Illegal Order Status Mutation:** Reviving cancelled orders or changing completed orders. | Low (1) | Critical (3) | **3 (Med)** | Attempted invalid status transitions via `PATCH`. Confirmed rejection with `422 Unprocessable Entity`. | TC-015, TC-023 |
| **RSK-008** | **Unauthenticated Access:** Public requests succeed without authentication headers. | Low (1) | Critical (3) | **3 (Med)** | Tested omitting `X-API-KEY`. Verified immediate rejection with `401 Unauthorized`. | TC-010 |

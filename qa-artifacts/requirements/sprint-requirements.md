# Sprint Requirements & Jira Ticket (SB-1042)

**Ticket Key:** `SB-1042`  
**Feature:** B2B Purchase Order Lifecycle & Inventory Integrity  
**Sprint:** Sprint 24  
**QA Tester:** Priya Murali  

---

## 1. User Story

**As a** registered B2B corporate customer,  
**I want to** place purchase orders through the REST API,  
**So that** I can order warehouse items with immediate price calculation, real-time stock allocation, and lifecycle tracking.

---

## 2. Business Acceptance Criteria I Verified

During this sprint, I took the business requirements and translated them into 10 clear acceptance criteria:

1. **REQ-001 (Customer Check):** Orders can only be placed for existing, active customers (`status: ACTIVE`). Suspended accounts are rejected.
2. **REQ-002 (Product Check):** Orders can only be placed for valid products in the catalog. Non-existent product IDs return `404 Not Found`.
3. **REQ-003 (Inventory Limit):** Orders requesting more units than currently available in warehouse stock must be rejected with `422 Unprocessable Entity` without deducting any stock.
4. **REQ-004 (Inventory Deduction):** When an order is successfully placed, the product's `available_stock` must decrease by the exact ordered quantity immediately.
5. **REQ-005 (Total Amount & Persistence):** The system must generate an order number (e.g., `ORD-2026-XXXXXXXX`), save the order row in the database, and calculate `total_amount = unit_price * quantity` rounded to two decimal places.
6. **REQ-006 (Order Retrieval):** An order can be retrieved by its ID via `GET /api/v1/orders/{id}`, matching the saved database record.
7. **REQ-007 (Cancellation & Stock Restored):** A customer can cancel a `CONFIRMED` order. Upon cancellation, the ordered units must be automatically returned to warehouse inventory. Completed orders cannot be cancelled.
8. **REQ-008 (Status Transitions):** Valid transitions: `CONFIRMED` -> `COMPLETED` or `CANCELLED`. Orders cannot be revived once cancelled.
9. **REQ-009 (Authentication & Tenant Isolation):** Requests require the `X-API-KEY` header. Customers are strictly forbidden from viewing or placing orders for another company (`403 Forbidden`).
10. **REQ-010 (Idempotency Protection):** When an `idempotencyKey` is supplied, retrying an identical request returns the existing order rather than creating a duplicate charge or reducing stock twice.

---

## 3. QA Definition of Done (DoD)

I marked this feature ready when:
- All 10 acceptance criteria were mapped to test cases and executed.
- All high-risk boundary and security scenarios passed.
- Database changes were directly validated using SQL.
- Zero open Critical or High defects remained.
- Test documentation and Postman artifacts were updated in the repository.

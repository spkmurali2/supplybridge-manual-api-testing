# Exploratory Testing Charter & My Session Log

**Tester:** Priya Murali  
**Charter:** Explore Order Creation boundaries, inventory deduction, and authorization rules.  
**Timebox:** 60 minutes  
**Environment:** Local test environment (`http://localhost:8081`)  

---

## 1. What I Explored

Before writing my formal test cases, I explored the API using real heuristics:
- **Boundaries:** Zero quantity (`0`), negative quantity (`-5`), and numbers exceeding warehouse stock (`99999`).
- **Data Types:** String in numeric fields (`quantity: "ten"`), empty JSON `{}`.
- **Security:** Missing `X-API-KEY` header, fake API keys, and requesting another customer's order ID.
- **Database Side Effects:** Running SQL queries before and after each request to ensure rejected calls didn't secretly modify the database.

---

## 2. My Session Observations & Log

| # | What I Tested | What I Expected | What Actually Happened | Notes / Evidence |
| :- | :--- | :--- | :--- | :--- |
| **1** | `GET /api/v1/customers` | Return active customer records. | Returned `200 OK` with 4 customer profiles. | Confirmed test customers exist in DB. |
| **2** | `GET /api/v1/products` | Return products with stock levels. | Returned `200 OK` with 5 items. Product 2 has 120 units. | Recorded baseline stock for later checks. |
| **3** | `POST /api/v1/orders` without `X-API-KEY` | Block request as unauthenticated. | Returned `401 Unauthorized` (`MISSING_HEADER`). | Security gate works as expected. |
| **4** | `POST /api/v1/orders` Customer 1 ordering for Customer 2 | Block cross-tenant action. | Returned `403 Forbidden` (`FORBIDDEN`). | Customer 1 cannot place orders for Customer 2. |
| **5** | `POST /api/v1/orders` with `quantity: 9999` (available: 110) | Reject due to insufficient inventory. | Returned `422 Unprocessable Entity` with error message. | Handled gracefully without server 500 error. |
| **6** | Verified database after step 5 | Stock remains untouched. | Ran `SELECT available_stock FROM products WHERE id=2;` ➔ Still 110. | Confirmed no accidental deduction occurred. |
| **7** | `POST /api/v1/orders` with valid payload (qty: 10) | Order created and stock decremented. | Returned `201 Created` with order number. Stock decreased to 100. | Core happy path passed. |
| **8** | Resent same request with identical `idempotencyKey` | Return existing order without creating a duplicate. | Returned existing order; stock remained at 100 in DB. | Duplicate protection verified. |
| **9** | `POST /api/v1/orders/{id}/cancel` | Order marked CANCELLED and stock restored. | Returned `200 OK`. Stock in DB restored back to 110. | Cancellation & stock refund verified. |

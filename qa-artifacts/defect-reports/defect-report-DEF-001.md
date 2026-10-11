# Defect Report: DEF-001

**Reporter:** Priya Murali  
**Sprint:** Sprint 24  
**Severity:** Critical  
**Priority:** High  
**Status:** **Closed** (Retested & Verified)  

---

## 1. Defect Title
API accepts orders exceeding available warehouse stock and sets negative inventory.

---

## 2. Description & Impact
When placing an order with a quantity greater than the warehouse's `available_stock`, the API accepted the order, returned `201 Created`, and decreased the stock into negative numbers. 

**Business Impact:** Customers would be billed for out-of-stock items, resulting in unfulfillable orders and corrupted warehouse inventory.

---

## 3. Steps I Followed to Reproduce

1. Selected Product 4 (`Direct Thermal Shipping Labels`), which had `available_stock = 0`.
2. Opened Postman and sent a `POST` request to `/api/v1/orders`:
   ```json
   {
       "customerId": 1,
       "productId": 4,
       "quantity": 10
   }
   ```
3. Looked at the response status code and body.
4. Queried the database:
   ```sql
   SELECT id, sku, available_stock FROM products WHERE id = 4;
   ```

---

## 4. Expected vs. Actual Result

- **Expected Result:**  
  API should return `422 Unprocessable Entity` with an error message: `"Insufficient inventory"`. In the database, no order row should be created and stock should remain `0`.
- **Actual Result (Before Fix):**  
  API returned `201 Created` with a new order number. In the database, `available_stock` dropped from `0` to `-10`.

---

## 5. Developer Fix & My Retest

- **Code Fix Applied:** The developer added a validation check in `OrderService.java` prior to inventory deduction:
  ```java
  if (product.getAvailableStock() < request.getQuantity()) {
      throw new BusinessRuleException("Insufficient inventory. Requested: " + request.getQuantity()
              + ", available stock: " + product.getAvailableStock());
  }
  ```
- **My Retest:**  
  I re-executed the request with a quantity of 9999 units.
  - The API responded with: **`422 Unprocessable Entity`**.
  - Response body: `"Insufficient inventory. Requested: 9999, available stock: 110"`.
  - Database check confirmed that stock remained at 110 and zero rows were added to the `orders` table.
- **Resolution:** Marked **Closed**.

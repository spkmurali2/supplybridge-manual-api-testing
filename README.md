# SupplyBridge — B2B Manual API Testing & SQL Validation Portfolio

Hi! I am **Priya Murali**, and this is my hands-on QA portfolio project. 

In this project, I simulated testing a real B2B order and inventory backend API during a Scrum sprint. Rather than just relying on mocked responses, I set up the actual backend application, executed comprehensive manual test cases in Postman, and directly validated database records and inventory levels using SQL queries.

---

## 🎯 What is SupplyBridge?

SupplyBridge is a fictional B2B wholesale platform where business customers:
- Browse wholesale products and live warehouse stock.
- Place bulk purchase orders with calculated unit prices.
- Track their order status (`PENDING` ➔ `CONFIRMED` ➔ `COMPLETED`).
- Cancel orders and verify that warehouse inventory is accurately restored.

---

## 💡 What I Tested as a QA Engineer

In API testing, checking for `200 OK` is not enough. I designed tests to catch real business failures:
1. **Financial Calculations:** Verified that `totalAmount` strictly equals `unitPrice * quantity` with proper decimal rounding.
2. **Inventory Integrity (Overselling):** Ensured that an order requesting more stock than available gets rejected (`422 Unprocessable Entity`) and does not corrupt stock.
3. **Database Consistency:** After placing or cancelling orders, I ran direct SQL queries against the database to confirm that rows were saved correctly and inventory counts were decremented or restored.
4. **Multi-Tenant Security (BOLA / IDOR):** Proved that Customer 1 cannot view or modify Customer 2's orders (`403 Forbidden`).
5. **Idempotency (Duplicate Prevention):** Tested network retry protection so resending identical orders doesn't double-charge customers or deduct stock twice.

---

## 🛠️ Tools & Technologies Used

- **Postman:** Manual request execution, collection organization, environment variables, and test assertions.
- **SQL / Relational Database:** Direct database verification queries (PostgreSQL / H2).
- **Backend Application:** Java 17 + Spring Boot (Application Under Test).
- **Contract Spec:** OpenAPI 3.0.
- **Git & GitHub:** Version control and portfolio documentation.

---

## 📁 Repository Structure

```text
├── README.md                                  # Project overview and testing guide
├── app/                                       # The actual running API backend
├── database/                                  # Database schema and seed scripts
├── api-spec/                                  # OpenAPI 3.0 contract definition
└── qa-artifacts/                              # My QA documentation
    ├── requirements/                          # Sprint user story & acceptance criteria (SB-1042)
    ├── risk-analysis/                         # Risk register and test prioritisation
    ├── exploratory-testing/                   # My exploratory testing notes & heuristics
    ├── test-plan/                             # Sprint test plan
    ├── test-cases/                            # 28 Detailed manual test cases
    ├── postman/                               # Importable Postman collection & environment
    ├── sql-validation/                        # Verification SQL queries
    ├── defect-reports/                        # Defect report (DEF-001) & defect lifecycle
    ├── execution-results/                     # Test execution metrics & results log
    └── test-summary/                          # My sprint QA sign-off report
```

---

## 🚀 How to Run and Test This Project Locally

### Step 1: Start the Backend Application
Make sure you have Java 17 and Maven installed, then run:
```powershell
cd app
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```
Once started, the API runs on `http://localhost:8081`.

### Step 2: Open the Database Console in Your Browser
Open your browser and visit: **`http://localhost:8081/h2-console`**
- **JDBC URL:** `jdbc:h2:mem:supplybridge_db`
- **User Name:** `sa`
- **Password:** *(leave blank)*
- Click **Connect** to query the live database tables!

### Step 3: Import into Postman
1. Open Postman.
2. Click **Import** and select the two files from `qa-artifacts/postman/`:
   - `SupplyBridge-Manual-API-Testing.postman_collection.json`
   - `SupplyBridge-Local-Test.postman_environment.json`
3. In the top-right environment dropdown, select **`SupplyBridge Local Test Environment`**.

---

## 🔍 How I Validated the Database with SQL

Here is the exact manual testing workflow I followed:

1. **Check Initial Stock:**
   ```sql
   SELECT id, name, unit_price, available_stock FROM products WHERE id = 2;
   -- Result: Product 2 had 120 units in stock.
   ```
2. **Place an Order in Postman:**
   Sent a `POST` request to `/api/v1/orders` ordering `10` units.
   - Response: `201 Created` with `totalAmount: 147.50`.
3. **Verify Database Deduction:**
   ```sql
   SELECT id, available_stock FROM products WHERE id = 2;
   -- Result: Available stock immediately dropped to 110 units!
   ```
4. **Cancel Order and Verify Restoration:**
   Sent a `POST` request to `/api/v1/orders/100/cancel`.
   ```sql
   SELECT id, available_stock FROM products WHERE id = 2;
   -- Result: Stock was restored back to 120 units!
   ```

---

## 📊 Summary of My QA Deliverables

| Deliverable | Location | What I Did |
| :--- | :--- | :--- |
| **Requirements Analysis** | [`qa-artifacts/requirements/sprint-requirements.md`](qa-artifacts/requirements/sprint-requirements.md) | Broke down Jira ticket SB-1042 into 10 testable acceptance criteria (REQ-001 to REQ-010). |
| **Risk Analysis** | [`qa-artifacts/risk-analysis/risk-register.md`](qa-artifacts/risk-analysis/risk-register.md) | Ranked 8 key business risks (overselling, data leakage, rounding bugs) to prioritize high-risk tests first. |
| **Exploratory Testing** | [`qa-artifacts/exploratory-testing/exploratory-session-log.md`](qa-artifacts/exploratory-testing/exploratory-session-log.md) | Executed a 60-minute session testing boundary values, invalid payloads, and security headers. |
| **Test Cases** | [`qa-artifacts/test-cases/test-cases.md`](qa-artifacts/test-cases/test-cases.md) | Authored 28 test cases covering positive paths, negative inputs, boundary checks, and security. |
| **Postman Suite** | [`qa-artifacts/postman/`](qa-artifacts/postman/) | Built a 5-folder collection with parameterized variables and assertion scripts. |
| **SQL Queries** | [`qa-artifacts/sql-validation/validation-queries.sql`](qa-artifacts/sql-validation/validation-queries.sql) | Prepared and executed SQL verification scripts. |
| **Defect Report** | [`qa-artifacts/defect-reports/defect-report-DEF-001.md`](qa-artifacts/defect-reports/defect-report-DEF-001.md) | Documented an illustrative Critical defect (inventory overselling bug) with reproduction steps and retest evidence. |
| **Sign-Off Report** | [`qa-artifacts/test-summary/sprint-test-summary.md`](qa-artifacts/test-summary/sprint-test-summary.md) | Completed sprint quality evaluation and provided my formal release sign-off. |

---

## 👩‍💻 Author
**Priya Murali**  
*Manual API Testing & QA Engineering Portfolio*

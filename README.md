# SupplyBridge – Manual API Testing Project

## About the Project

SupplyBridge is a sample B2B order management API testing project. It represents a wholesale platform where business customers can view products, place orders, check stock, and cancel orders.

The aim of this project is to practise manual API testing using Postman and verify backend data using SQL.

## What I Tested

- Order creation, order cancellation, and order status changes.
- Product prices, order totals, and inventory updates.
- Invalid requests, missing fields, and boundary values.
- Stock availability and attempts to order more than the available quantity.
- Access control to check whether customers can access other customers' orders.
- Duplicate requests and their effect on orders and inventory.
- Database records before and after API requests.

## Tools Used

- Java 17 and Spring Boot
- Postman
- SQL and H2 database
- OpenAPI 3.0
- Git and GitHub

## Project Structure

```text
SupplyBridge/
├── app/                    # Backend application
├── database/               # Database scripts
├── api-spec/               # API specification
└── qa-artifacts/
    ├── requirements/       # Requirements and acceptance criteria
    ├── risk-analysis/     # Risk register
    ├── exploratory-testing/
    ├── test-plan/
    ├── test-cases/        # Manual test cases
    ├── postman/           # Postman collection and environment
    ├── sql-validation/    # SQL validation queries
    ├── defect-reports/    # Defect documentation
    ├── execution-results/
    └── test-summary/      # Test summary and sign-off
```

## How to Run the Project

### 1. Start the application

Install Java 17 and Maven first. From the project root, run:

```bash
cd app
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

The API runs at `http://localhost:8081`.

### 2. Open the database console

Open `http://localhost:8081/h2-console` in your browser.

Use these details:

- **JDBC URL:** `jdbc:h2:mem:supplybridge_db`
- **Username:** `sa`
- **Password:** Leave blank

Click **Connect** to view and query the database.

### 3. Import the Postman files

Import these files from `qa-artifacts/postman/` into Postman:

- `SupplyBridge-Manual-API-Testing.postman_collection.json`
- `SupplyBridge-Local-Test.postman_environment.json`

Select the SupplyBridge local environment before running requests.

## Database Validation Example

I used SQL queries to check whether API requests updated the database correctly.

For example, I checked the stock for product ID 2 before placing an order.

```sql
SELECT id, name, unit_price, available_stock
FROM products
WHERE id = 2;
```

The initial stock was 120 units. After placing an order for 10 units, I checked the database again and confirmed that the stock had decreased to 110 units.

After cancelling the order, I checked the stock once more to verify whether it had returned to 120 units.

## QA Deliverables

| Deliverable | Description |
|---|---|
| Requirements | Acceptance criteria for the main business requirements |
| Risk register | Key risks related to orders, stock, pricing, and access control |
| Test cases | 28 manual test cases covering positive and negative scenarios |
| Postman collection | API requests organised into five folders |
| SQL queries | Queries for checking order and inventory data |
| Defect report | Documentation for the inventory overselling defect |
| Test summary | Test execution results and QA sign-off |

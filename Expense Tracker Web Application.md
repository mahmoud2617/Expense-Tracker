
## 1. Project Goal

Build a web-based personal expense tracking application that allows users to record, organize, search, analyze, and manage their daily expenses.

The goal is to practice building a complete backend application with **Spring Boot**, including REST APIs, database persistence, validation, business logic, exception handling, and a simple frontend.

---

# 2. Business Goals

The application should help a user:

- Keep track of personal expenses.
- Understand where their money is going.
- Categorize expenses.
- Quickly find previous expenses.
- See total spending.
- Analyze spending by category.
- Identify their most expensive expenses.
- Review spending over a specific period.
- Correct or remove incorrectly recorded expenses.
- Get a simple overview of their financial activity.


The system should prioritize:

- Simplicity
- Accuracy
- Fast interaction
- Clear financial information
- Good data validation
- Clean API design
- Maintainable backend architecture


---

# 3. Core Features

## 3.1 Create Expense

The user can create an expense.

Required information:

- Amount
- Category
- Description
- Expense date

Example:

```text
Amount: 250.00
Category: FOOD
Description: Dinner
Date: 2026-09-11
```

The system should automatically generate:

- Expense ID
- Creation timestamp
- Last update timestamp

### Business rules

- Amount must be greater than 0.
- Description cannot be empty.
- Category must be one of the supported categories.
- Date cannot be invalid.
- Currency should be consistent throughout the application.

---

# 3.2 View All Expenses

The user can view their expenses.

Each expense should display:

```text
ID
Amount
Category
Description
Date
Created At
```

Expenses should be ordered by date, with the newest expenses first.

---

# 3.3 View Single Expense

The user can retrieve one expense using its ID.

Example:

```text
GET /api/expenses/{id}
```

If the expense doesn't exist, the API should return an appropriate error response.

---

# 3.4 Update Expense

The user can modify an existing expense.

The user should be able to change:

- Amount
- Category
- Description
- Date

The system should update the `updatedAt` timestamp automatically.

---

# 3.5 Delete Expense

The user can delete an expense.

Before deleting, the frontend should ask for confirmation.

Example:

```text
Are you sure you want to delete this expense?
```

If the expense does not exist, return an appropriate error.

---

# 3.6 Categories

Create a fixed set of expense categories.

Example:

```text
FOOD
TRANSPORT
ENTERTAINMENT
SHOPPING
HEALTH
EDUCATION
BILLS
RENT
TRAVEL
OTHER
```

Use a Java `enum` rather than storing arbitrary strings.

Example:

```java
public enum ExpenseCategory {
    FOOD,
    TRANSPORT,
    ENTERTAINMENT,
    SHOPPING,
    HEALTH,
    EDUCATION,
    BILLS,
    RENT,
    TRAVEL,
    OTHER
}
```

---

# 3.7 Search Expenses

The user should be able to search expenses by description.

Example:

```text
Search: dinner
```

The system should return expenses whose descriptions contain the search text.

The search should preferably be case-insensitive.

---

# 3.8 Filter by Category

The user should be able to filter expenses.

Example:

```text
Category: FOOD
```

Only food-related expenses should be returned.

---

# 3.9 Filter by Date

Allow users to filter expenses by date range.

Example:

```text
From: 2026-09-01
To:   2026-09-11
```

The system returns expenses recorded during that period.

---

# 3.10 Combined Filtering

Users should be able to combine filters.

For example:

```text
Category: FOOD
From: 2026-09-01
To: 2026-09-11
Search: dinner
```

The backend should apply all provided filters.

---

# 3.11 Calculate Total Spending

Provide an endpoint that calculates the total amount spent.

Example:

```text
Total spending:
EGP 7,450.00
```

The total should support optional date filtering.

Example:

```text
Total spent this month
Total spent this year
Total spent between two dates
```

---

# 3.12 Spending by Category

Calculate how much money was spent in each category.

Example:

```text
FOOD            EGP 2,500
TRANSPORT       EGP 1,200
SHOPPING        EGP 1,750
ENTERTAINMENT   EGP   800
BILLS           EGP 1,200
```

This should be calculated by the backend rather than by JavaScript.

---

# 3.13 Most Expensive Expense

Provide functionality to find the most expensive expense.

Example:

```text
Most expensive expense

EGP 4,500
Category: RENT
Description: September Rent
```

---

# 3.14 Dashboard

Create a simple dashboard as the main page.

The dashboard should display:

### Total Spending

```text
EGP 7,450
```

### Number of Expenses

```text
32 expenses
```

### Most Expensive Expense

```text
EGP 4,500
```

### Current Month Spending

```text
EGP 3,200
```

### Spending by Category

Display a simple chart or visual breakdown.

---

# 4. Sorting

The expense list should support sorting.

Possible sorting options:

```text
Newest
Oldest
Highest amount
Lowest amount
```

The sorting should preferably be handled by the backend.

Example:

```text
GET /api/expenses?sort=amount,desc
```

---

# 5. Pagination

Do not load thousands of expenses into the browser at once.

Implement pagination.

Example:

```text
GET /api/expenses?page=0&size=10
```

The response should contain:

- Expenses
- Current page
- Page size
- Total elements
- Total pages

Use Spring Data's `Pageable`.

---

# 6. Validation

The backend must validate incoming requests.

Example rules:

### Amount

```text
amount > 0
```

### Description

```text
required
maximum length: 255
```

### Category

```text
must be a valid category
```

### Date

```text
required
```

Use Jakarta Bean Validation.

For example:

```java
@NotNull
@Positive
private BigDecimal amount;
```

---

# 7. Error Handling

Create centralized exception handling using:

```text
@RestControllerAdvice
```

The API should return consistent error responses.

Example:

```json
{
    "status": 400,
    "message": "Amount must be greater than zero",
    "timestamp": "2026-09-11T18:30:00"
}
```

Handle cases such as:

- Expense not found
- Invalid request
- Invalid category
- Invalid date
- Validation errors
- Unexpected server errors

---

# 8. REST API

Design a clean REST API.

Suggested endpoints:

## Expenses

```text
POST   /api/expenses
GET    /api/expenses
GET    /api/expenses/{id}
PUT    /api/expenses/{id}
DELETE /api/expenses/{id}
```

## Statistics

```text
GET /api/expenses/statistics/total
GET /api/expenses/statistics/by-category
GET /api/expenses/statistics/most-expensive
```

You may design better endpoint structures if you find a cleaner approach.

---

# 9. Suggested Database Model

Create an `expenses` table.

Possible fields:

```text
id
amount
category
description
expense_date
created_at
updated_at
```

Example:

```text
expenses
------------------------------------------------
id              BIGINT
amount          DECIMAL
category        VARCHAR
description     VARCHAR
expense_date    DATE
created_at      TIMESTAMP
updated_at      TIMESTAMP
```

Use appropriate database types rather than storing everything as strings.

---

# 10. Database Requirements

Use:

**PostgreSQL**

Use:

**Spring Data JPA / Hibernate**

Use:

**Flyway**

Do not rely on Hibernate's automatic schema generation for production-style database management.

Prefer:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

and manage schema changes through Flyway migrations.

Example:

```text
V1__create_expenses_table.sql
```

---

# 11. Backend Architecture

Use a layered architecture.

Keep responsibilities separated.

### Controller

Responsible for:

- HTTP requests
- HTTP responses
- Request validation
- Calling services

### Service

Responsible for:

- Business rules
- Calculations
- Filtering logic
- Statistics
- Transaction boundaries

### Repository

Responsible for:

- Database access
- Queries

### DTOs

Responsible for:

- API input/output models

Avoid exposing JPA entities directly through REST API.

---

# 12. Frontend

Build a simple frontend using:

- HTML
- CSS
- Vanilla JavaScript

Do not use React, Angular, Vue, or another frontend framework.

---

# 13. Frontend Pages

## Dashboard

Display:

- Total spending
- Number of expenses
- Current month spending
- Most expensive expense
- Category breakdown

---

## Expenses Page

Display a table:

```text
Date | Description | Category | Amount | Actions
```

Actions:

```text
Edit
Delete
```

---

## Add Expense

Create a form:

```text
Amount
Category
Description
Date
```

Submit the form using JavaScript `fetch()`.

Do not reload the entire page unnecessarily.

---

## Edit Expense

Reuse the expense form for editing.

---

# 14. Frontend Search & Filters

Provide:

```text
Search
Category
From Date
To Date
Sort
```

Example:

```text
Search: groceries

Category: FOOD

From: 2026-09-01
To:   2026-09-30

Sort: Highest amount
```

The frontend should send these parameters to the backend.

---

# 15. User Experience

Keep the UI simple.

Prioritize:

- Clear typography
- Good spacing
- Readable tables
- Clear buttons
- Responsive layout
- Useful empty states
- Loading indicators
- Error messages
- Confirmation before destructive actions

---

# 16. Currency

Use:

```text
EGP
```

as the default currency.

Do not use `double` or `float` for money.

Use:

```java
BigDecimal
```

instead.

---

# 17. Recommended Tech Stack

## Backend

- Java 21+
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Jakarta Bean Validation
- PostgreSQL
- Flyway
- Lombok
- MapStruct
- Maven

## Frontend

- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

## Development Tools

- Docker
- Docker Compose

## Database

- PostgreSQL

---

# 18. Docker

Create a Docker Compose setup for the entire app.

The application should be able to run using Docker without requiring PostgreSQL to be manually installed on the machine.

---

# 19. Configuration

Use environment variables for configuration.

For example:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
```

Do not commit passwords or secrets to GitHub.

Use:

```text
application.yaml
application-dev.yaml
```

---

# 20. Testing

Write backend tests.

At minimum:

### Unit Tests

Test the service layer.

Examples:

```text
create expense
update expense
delete expense
calculate total
calculate category totals
find most expensive expense
```

### Repository Tests

Test important database queries.

### Controller Tests

Test important HTTP endpoints and validation behavior.

Focus on meaningful business behavior.

---

# 21. Business Rules

The following rules must always be enforced by the backend:

1. An expense must have a positive amount.
2. An expense must have a valid category.
3. An expense must have a description.
4. An expense must have a date.
5. An expense ID cannot be changed.
6. An expense cannot be updated if it doesn't exist.
7. An expense cannot be deleted if it doesn't exist.
8. Monetary values must use `BigDecimal`.
9. Statistics must be calculated from persisted data.
10. Database schema changes must be managed through Flyway.
11. API validation must happen on the backend even if the frontend validates the same fields.
12. The API must return appropriate HTTP status codes.

---

# 22. HTTP Status Codes

Use appropriate HTTP responses.

---

# 23. README

Project should contain a proper README.

Include:

- Project description
- Features
- Tech stack
- Architecture
- Database schema
- API endpoints
- How to run locally
- Docker instructions
- Environment variables
- Screenshots
- Example API requests

---

# 24. What You Should NOT Add Initially

Avoid:

- React
- Microservices
- Kafka
- Kubernetes
- Complex authentication
- Cloud deployment
- Complicated design systems
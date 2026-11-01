# Project Report – Web-Based Banking Management System

## 1. Abstract
A web application that lets customers register, log in, view their balance, deposit, withdraw, transfer money and view history, and lets an administrator view customers, accounts, transactions and audit logs. It is built with Java Servlets, JSP, JDBC and MySQL and demonstrates Advanced Internet Programming and database programming concepts.

## 2. Objectives
Secure registration/login · deposits, withdrawals, transfers · transaction history · read-only admin dashboard · audit trail.

## 3. Technologies
Java 21, Apache Tomcat 10.1, Jakarta Servlet 6 / JSP 3.1 / JSTL 3, JDBC, MySQL 8, Maven, HTML/CSS/JavaScript.

## 4. Architecture
Presentation (JSP, CSS, JS) → Application (Servlets, Filter, DAOs) → Database (MySQL tables, procedures, trigger).
Request flow: `Browser → Servlet (validate, session) → DAO → JDBC → MySQL`, then `Servlet → forward → JSP → Browser`.

## 5. Database design (6 tables)
```
branches 1──* accounts *──1 customers 1──0..1 users
accounts 1──* transactions
accounts 1──* audit_logs
```
| Table | Key columns |
|---|---|
| branches | branch_id PK, branch_code (unique), ifsc_code (unique) |
| customers | customer_id PK, email (unique) |
| users | user_id PK, customer_id FK (NULL for admin), username (unique), password_hash, role |
| accounts | account_id PK, customer_id FK, branch_id FK, account_number (unique), balance DECIMAL(15,2) ≥ 0 |
| transactions | transaction_id PK, account_id FK, type, amount > 0, balance_after, reference_account |
| audit_logs | audit_id PK, account_id FK, action_type, old_balance, new_balance |

A transfer is stored as two `transactions` rows (TRANSFER_DEBIT for the sender, TRANSFER_CREDIT for the receiver).

## 6. Modules
Registration · Login · Dashboard · Deposit · Withdraw · Fund transfer · Transaction history · Admin dashboard (see README and CONCEPTS.md).

## 7. Security
PBKDF2-HMAC-SHA256 hashing (65,536 iterations, random 16-byte salt) · PreparedStatement · session auth + new session at login · role-based `AuthFilter` · `<c:out>` output escaping · server-side validation (amount > 0, ≤ 2 decimals, ≤ ₹1 crore; 10-digit phone; password rules) · DB constraints · no-cache headers on private pages · same error for wrong user/password.

## 8. Testing
| # | Test | Expected |
|---|---|---|
| 1 | Register new customer | Account created, redirect to login |
| 2 | Duplicate email | Error message |
| 3 | Wrong password | "Invalid email/username or password" |
| 4 | Deposit ₹1,000 (Aarav) | Balance 11,000; transaction + audit row |
| 5 | Withdraw ₹500 | Balance 10,500 |
| 6 | Transfer ₹2,000 → 100000000002 | Aarav −2,000, Diya +2,000; 2 transactions, 2 audit rows |
| 7 | Withdraw > balance | "Insufficient balance", nothing changes |
| 8 | Transfer to invalid account | "Receiver account not found" |
| 9 | Negative / zero / text amount | Rejected |
| 10 | Open /dashboard logged out | Redirect to login |
| 11 | Customer opens /admin | 403 |
| 12 | Admin dashboard | Counts, tables and audit logs shown |

Add screenshots of each page here before submission.

## 9. Limitations / future scope
Educational simulation; no email/OTP, no CSRF tokens, no admin write operations, single account per customer, no connection pool (a pool such as HikariCP would be used in production).

## 10. Conclusion
The project demonstrates a layered Java web application with secure authentication, transactional money handling in the database and an automatic audit trail.

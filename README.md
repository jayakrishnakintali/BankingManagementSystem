# Web-Based Banking Management System

College mini-project: Java 21 · Apache Tomcat 10.1 · Jakarta Servlets · JSP/JSTL · JDBC · MySQL 8 · Maven.
**Educational simulation only – not for real financial transactions.**

## 1. Install (in this order)
1. **JDK 21** – set `JAVA_HOME`; check `java -version`.
2. **MySQL Server 8.x + MySQL Workbench** – remember the root password.
3. **Apache Tomcat 10.1.x** (must be 10.1 – it uses `jakarta.servlet.*`).
4. **Maven 3.9+** (or use the Maven bundled with Eclipse/IntelliJ).
5. **Eclipse IDE for Enterprise Java** or **IntelliJ IDEA**.

## 2. Create the database
1. Open MySQL Workbench → open `database/banking_system.sql` → run the whole script (lightning icon).
2. Check: `SHOW TABLES;` → 6 tables; `SHOW PROCEDURE STATUS WHERE Db='banking_system';` → 3 procedures.
3. The script is safe to run again – it never deletes existing data.

## 3. Configure the connection
Edit `src/main/resources/db.properties` and set `db.user` / `db.password` for your MySQL.

## 4. Build
```bash
mvn clean package          # creates target/BankingManagementSystem.war
```
(Optional JDBC test after `mvn compile`: run `com.banking.config.DBUtil` – it prints "JDBC connection OK".)

## 5. Run on Tomcat
* **Command line:** copy `target/BankingManagementSystem.war` into `<TOMCAT>/webapps/`, then run `bin/startup.bat` (Windows) or `bin/startup.sh`.
* **Eclipse:** import as *Existing Maven Project* → Servers view → add Tomcat 10.1 → *Run on Server*.
* **IntelliJ (Ultimate):** Run/Debug Configuration → Tomcat Server → Local → Deployment: `BankingManagementSystem:war exploded`, context `/BankingManagementSystem`.

Open: **http://localhost:8080/BankingManagementSystem/**

## 6. Demo logins
| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `Admin@123` |
| Customer (A/c 100000000001, ₹10,000) | `aarav@example.com` | `Aarav@123` |
| Customer (A/c 100000000002, ₹5,000) | `diya@example.com` | `Diya@123` |

New customers can register; their account number is generated automatically (starting balance ₹0 – deposit to add money).

## 7. Test checklist (Phase 8)
1. Register a user → login → logout.
2. Aarav: deposit ₹1,000 → balance 11,000. Withdraw ₹500 → 10,500.
3. Aarav → transfer ₹2,000 to `100000000002` → Aarav 8,500, Diya 7,000.
4. Withdraw more than the balance → "Insufficient balance"; transfer to `999999999999` → "Receiver account not found".
5. Open `/dashboard` while logged out → redirected to login. As a customer open `/admin` → 403.
6. Admin → check transactions and **audit logs** (rows created by the trigger).

## 7b. Quick SQL checks
```sql
SELECT * FROM transactions ORDER BY transaction_id DESC;
SELECT * FROM audit_logs ORDER BY audit_id DESC;
CALL sp_deposit(1, 100.00, @s); SELECT @s;
```

## 8. Troubleshooting
| Problem | Fix |
|---|---|
| `Access denied for user` | Wrong `db.user`/`db.password` in `db.properties` |
| `Communications link failure` | MySQL not running / wrong port in `db.url` |
| `ClassNotFoundException: javax.servlet...` / 404 for everything | You are on Tomcat 9 – use **Tomcat 10.1** |
| `PROCEDURE sp_deposit does not exist` | Run the full SQL script in Workbench |
| JSTL / `jakarta.tags.core` not found | Run `mvn clean package` so JSTL jars go into `WEB-INF/lib` |
| Unicode `₹` shows as `?` | Keep files UTF-8 (already set in `pom.xml`) |

## 9. Project structure
```
database/banking_system.sql   tables, sample data, trigger, 3 stored procedures
src/main/java/com/banking/
  config/DBUtil.java          JDBC connection helper
  model/                      User, Customer, Account, Transaction
  dao/                        UserDAO, CustomerDAO, AccountDAO, TransactionDAO
  servlet/                    Login, Register, Logout, Dashboard, Deposit, Withdraw, Transfer, Transactions, Admin
  filter/AuthFilter.java      login + role protection
  util/                       PasswordUtil (PBKDF2), Validator, WebUtil
src/main/webapp/              index/login/register.jsp, WEB-INF/views/*.jsp, css, js
docs/                         PROJECT_REPORT.md, CONCEPTS.md, VIVA_QA.md
```

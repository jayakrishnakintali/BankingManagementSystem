# AIP and Database Programming Concepts – where to find them in the code

## Advanced Internet Programming
| Concept | Where / how |
|---|---|
| **Client–server architecture** | Browser (client) ⇄ Tomcat (web server, runs servlets/JSP) ⇄ MySQL (database server). |
| **Servlet lifecycle** | `LoginServlet`: `init()` (once, creates DAO), `service()` (every request, logs then calls `super.service()` which picks `doGet`/`doPost`), `destroy()` (on undeploy/shutdown). Watch the Tomcat log (`logs/catalina.*.log` or console). |
| **GET vs POST** | GET shows pages (`/login`, `/deposit` form, `/dashboard`). POST changes data (login, register, deposit, withdraw, transfer) – data is in the body, not the URL. |
| **HttpServletRequest / Response** | `req.getParameter()`, `req.setAttribute()`, `res.sendRedirect()`, `res.sendError()`, `res.setHeader()` in all servlets and `AuthFilter`. |
| **HttpSession** | `LoginServlet` stores `user` in the session; `AuthFilter` checks it; `LogoutServlet` calls `session.invalidate()`. A fresh session is created after login (prevents session fixation). |
| **RequestDispatcher** | `req.getRequestDispatcher("/WEB-INF/views/x.jsp").forward(req, res)` – servlet → JSP, URL stays the same. Views under `WEB-INF` cannot be opened directly. |
| **Redirect vs forward** | After a successful login/registration we `sendRedirect` (new request, Post-Redirect-Get). Pages that show results forward. |
| **Filter** | `AuthFilter` runs before protected servlets (role-based access). |
| **JSP directives** | `<%@ page %>`, `<%@ taglib %>`, `<%@ include file=... %>` (header/footer). |
| **JSP declaration / expression / scriptlet** | `index.jsp`: `<%! ... %>` declares `pageViews`, `<%= ... %>` prints date/views/server info. |
| **JSP implicit objects** | `application` (index.jsp), `session`, `request`, `pageContext` (`${pageContext.request.contextPath}`), `param`, `sessionScope` in EL. |
| **Expression Language + JSTL** | `${account.balance}`, `<c:forEach>`, `<c:if>`, `<c:choose>`, `<c:out>` (HTML-escapes → prevents XSS), `<fmt:formatNumber>`, `<fmt:formatDate>`. |
| **JDBC** | `DBUtil` (DriverManager), `PreparedStatement` (all queries), `CallableStatement` (`TransactionDAO.call`), `ResultSet`, `Statement.RETURN_GENERATED_KEYS`, manual transaction in `RegisterServlet` (`setAutoCommit(false)`, `commit`, `rollback`). CRUD: Create (register), Read (history), Update (balance via procedures), Delete is intentionally not offered for bank records. |

## Database programming (MySQL equivalent of PL/SQL)
| Concept | Where |
|---|---|
| Stored procedures | `sp_deposit`, `sp_withdraw`, `sp_transfer` (IN / OUT parameters; OUT `p_status`). |
| Trigger | `trg_accounts_audit` – AFTER UPDATE on `accounts`; inserts into `audit_logs` whenever balance changes. |
| Transactions | `START TRANSACTION … COMMIT / ROLLBACK` in each procedure. |
| Error handling | `DECLARE EXIT HANDLER FOR SQLEXCEPTION` → rollback + friendly status. |
| Control flow | `IF / ELSEIF`, `LEAVE` label, validation of amount, balance and account status. |
| Concurrency | `SELECT … FOR UPDATE` row locks; transfer locks rows in `account_id` order to avoid deadlocks. |
| Constraints | PK, FK, UNIQUE, `CHECK (balance >= 0)`, `CHECK (amount > 0)`, ENUMs, `DECIMAL(15,2)` for money, indexes. |

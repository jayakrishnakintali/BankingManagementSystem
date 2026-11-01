# Viva Questions and Answers

**1. What is a servlet? Explain its lifecycle.** A Java class that handles HTTP requests inside a container (Tomcat). Lifecycle: loaded → `init()` once → `service()` per request (calls `doGet`/`doPost`) → `destroy()` once. See `LoginServlet`.

**2. Why `jakarta.servlet` and not `javax.servlet`?** Tomcat 10+ implements Jakarta EE 9+, where packages were renamed. Tomcat 9 and older use `javax`.

**3. GET vs POST?** GET: data in URL, idempotent, for reading. POST: data in body, for changes/sensitive data (passwords, money).

**4. Forward vs redirect?** Forward is server-side (same request, URL unchanged). Redirect tells the browser to make a new request (URL changes); used after POST to avoid duplicate submissions.

**5. How is login state maintained?** HTTP is stateless; `HttpSession` stores the `User` object, linked to the browser by the JSESSIONID cookie.

**6. What does `AuthFilter` do?** Intercepts requests to private URLs; redirects to login if no session user, blocks customers from `/admin`.

**7. Why are the main JSPs inside WEB-INF?** Files under WEB-INF cannot be requested directly; they can only be reached through a servlet forward, so access control cannot be bypassed.

**8. JSP scripting elements?** Declaration `<%! %>`, scriptlet `<% %>`, expression `<%= %>`, comment `<%-- --%>`. Prefer EL/JSTL in real code.

**9. Name the JSP implicit objects.** request, response, session, application, out, config, pageContext, page, exception.

**10. Static vs dynamic include?** `<%@ include %>` merges files at translation time (we use it for header/footer); `<jsp:include>` includes at request time.

**11. What is JDBC and its steps?** API to connect Java to databases: load driver → get `Connection` → create statement → execute → process `ResultSet` → close (try-with-resources).

**12. Statement vs PreparedStatement vs CallableStatement?** Statement = plain SQL; PreparedStatement = precompiled with `?` parameters (SQL-injection safe); CallableStatement = calls stored procedures.

**13. What is SQL injection and how do you prevent it?** Malicious input altering a query (`' OR '1'='1`). Prevent with PreparedStatement, validation, least privilege.

**14. Why DECIMAL for money?** FLOAT/DOUBLE are approximate (0.1 + 0.2 ≠ 0.3); DECIMAL is exact. In Java we use `BigDecimal`.

**15. Why store a password hash, not the password?** If the DB leaks, passwords stay protected. PBKDF2 is slow on purpose and uses a random salt, so identical passwords give different hashes and brute force is costly.

**16. What are ACID properties?** Atomicity, Consistency, Isolation, Durability. Transfer = debit + credit + 2 transaction rows, all committed together or rolled back.

**17. How do you stop two people changing a balance at once?** `SELECT ... FOR UPDATE` locks the row inside a transaction; the transfer locks rows in account_id order to avoid deadlock.

**18. What is a trigger? Which one do you use?** Code that runs automatically on INSERT/UPDATE/DELETE. `trg_accounts_audit` writes to `audit_logs` whenever `accounts.balance` changes.

**19. What is a stored procedure and why use it?** Precompiled SQL logic stored in the DB. Business rules + transaction live next to the data, fewer round trips, consistent rules.

**20. PL/SQL vs MySQL procedures?** PL/SQL is Oracle's language; MySQL uses its own stored-program syntax (`DELIMITER`, `DECLARE`, `HANDLER`). Concepts (procedures, triggers, exceptions, transactions) are the same.

**21. How is a transfer stored?** Two rows in `transactions` (TRANSFER_DEBIT, TRANSFER_CREDIT) plus two audit rows; no separate transfers table.

**22. What does `@WebServlet("/login")` do?** Maps the servlet to a URL without `web.xml` (annotation-based configuration).

**23. How do you prevent XSS?** Output is escaped with `<c:out>`.

**24. What is MVC here?** Model = classes + DAOs, View = JSP, Controller = servlets.

**25. Limitations?** No CSRF token, no OTP, no connection pool, simulation only.

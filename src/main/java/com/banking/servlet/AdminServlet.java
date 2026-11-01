package com.banking.servlet;

import com.banking.dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/** Read-only administrator dashboard (AuthFilter allows only role ADMIN). */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO txDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            req.setAttribute("customerCount", customerDAO.count());
            req.setAttribute("accountCount", accountDAO.count());
            req.setAttribute("transactionCount", txDAO.count());
            req.setAttribute("customers", customerDAO.findAll());
            req.setAttribute("accounts", accountDAO.findAllWithOwner());
            req.setAttribute("transactions", txDAO.findAllForAdmin());
            req.setAttribute("auditLogs", txDAO.findAuditLogs());
        } catch (SQLException e) { throw new ServletException(e); }
        req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, res);
    }
}

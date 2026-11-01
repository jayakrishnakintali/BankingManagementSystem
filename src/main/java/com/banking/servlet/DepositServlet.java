package com.banking.servlet;

import com.banking.dao.*;
import com.banking.model.Account;
import com.banking.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/deposit")
public class DepositServlet extends HttpServlet {
    private static final String VIEW = "/WEB-INF/views/deposit.jsp";
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO txDAO = new TransactionDAO();

    @Override   // GET = just show the form
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            req.setAttribute("account", accountDAO.findByCustomerId(WebUtil.currentUser(req).getCustomerId()));
        } catch (SQLException e) { throw new ServletException(e); }
        req.getRequestDispatcher(VIEW).forward(req, res);
    }

    @Override   // POST = process the deposit
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            long customerId = WebUtil.currentUser(req).getCustomerId();
            Account acc = accountDAO.findByCustomerId(customerId);
            BigDecimal amount = Validator.parseAmount(req.getParameter("amount"));
            if (amount == null) {
                req.setAttribute("error", "Enter a valid amount greater than zero (max 2 decimals, up to 1,00,00,000).");
            } else {
                String status = txDAO.deposit(acc.getAccountId(), amount);
                WebUtil.report(req, status, "\u20B9" + amount + " deposited successfully.");
            }
            req.setAttribute("account", accountDAO.findByCustomerId(customerId));   // fresh balance
        } catch (SQLException e) { throw new ServletException(e); }
        req.getRequestDispatcher(VIEW).forward(req, res);
    }
}

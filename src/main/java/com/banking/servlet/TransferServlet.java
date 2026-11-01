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

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {
    private static final String VIEW = "/WEB-INF/views/transfer.jsp";
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO txDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            req.setAttribute("account", accountDAO.findByCustomerId(WebUtil.currentUser(req).getCustomerId()));
        } catch (SQLException e) { throw new ServletException(e); }
        req.getRequestDispatcher(VIEW).forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            long customerId = WebUtil.currentUser(req).getCustomerId();
            Account acc = accountDAO.findByCustomerId(customerId);
            String toAccount = req.getParameter("toAccount") == null ? "" : req.getParameter("toAccount").trim();
            BigDecimal amount = Validator.parseAmount(req.getParameter("amount"));

            if (!Validator.validAccountNumber(toAccount)) {
                req.setAttribute("error", "Receiver account number must be exactly 12 digits.");
            } else if (amount == null) {
                req.setAttribute("error", "Enter a valid amount greater than zero (max 2 decimals, up to 1,00,00,000).");
            } else {
                String status = txDAO.transfer(acc.getAccountId(), toAccount, amount);   // single DB transaction
                WebUtil.report(req, status, "\u20B9" + amount + " transferred to account " + toAccount + ".");
                if ("SUCCESS".equals(status)) toAccount = "";
            }
            req.setAttribute("toAccount", toAccount);
            req.setAttribute("account", accountDAO.findByCustomerId(customerId));
        } catch (SQLException e) { throw new ServletException(e); }
        req.getRequestDispatcher(VIEW).forward(req, res);
    }
}

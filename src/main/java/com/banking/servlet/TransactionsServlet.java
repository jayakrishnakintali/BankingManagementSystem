package com.banking.servlet;

import com.banking.dao.*;
import com.banking.model.Account;
import com.banking.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/transactions")
public class TransactionsServlet extends HttpServlet {
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO txDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            Account acc = accountDAO.findByCustomerId(WebUtil.currentUser(req).getCustomerId());
            req.setAttribute("account", acc);
            req.setAttribute("transactions", txDAO.findByAccount(acc.getAccountId(), 200));
        } catch (SQLException e) { throw new ServletException(e); }
        req.getRequestDispatcher("/WEB-INF/views/transactions.jsp").forward(req, res);
    }
}

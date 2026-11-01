package com.banking.servlet;

import com.banking.config.DBUtil;
import com.banking.dao.*;
import com.banking.model.Customer;
import com.banking.util.PasswordUtil;
import com.banking.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = trim(req.getParameter("fullName"));
        String email = trim(req.getParameter("email")).toLowerCase();
        String phone = trim(req.getParameter("phone"));
        String address = trim(req.getParameter("address"));
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");

        String error = null;
        if (name.length() < 2 || name.length() > 100) error = "Enter your full name (2-100 characters).";
        else if (!Validator.validEmail(email)) error = "Enter a valid email address.";
        else if (!Validator.validPhone(phone)) error = "Phone number must be exactly 10 digits.";
        else if (address.length() < 5 || address.length() > 255) error = "Enter your address (5-255 characters).";
        else if (!Validator.validPassword(password)) error = "Password must be 8+ characters with at least one letter and one digit.";
        else if (!password.equals(confirm)) error = "Passwords do not match.";

        try {
            if (error == null && customerDAO.emailExists(email)) error = "This email is already registered.";
            if (error != null) {
                req.setAttribute("error", error);
                req.setAttribute("fullName", name);      // keep what the user typed
                req.setAttribute("email", email);
                req.setAttribute("phone", phone);
                req.setAttribute("address", address);
                req.getRequestDispatcher("/register.jsp").forward(req, res);
                return;
            }
            registerCustomer(new Customer(name, email, phone, address), PasswordUtil.hash(password));
            res.sendRedirect(req.getContextPath() + "/login?registered=1");
        } catch (SQLException e) {
            throw new ServletException("Registration failed", e);
        }
    }

    /** customer + login + account are created in ONE transaction: all or nothing. */
    private void registerCustomer(Customer cu, String passwordHash) throws SQLException {
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                long customerId = customerDAO.insert(c, cu);
                userDAO.insertCustomerUser(c, customerId, cu.getEmail(), passwordHash);
                String accountNo = accountDAO.nextAccountNumber(c);
                accountDAO.insert(c, customerId, AccountDAO.DEFAULT_BRANCH_ID, accountNo, "SAVINGS");
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}

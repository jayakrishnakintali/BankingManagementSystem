package com.banking.servlet;

import com.banking.dao.UserDAO;
import com.banking.model.User;
import com.banking.util.PasswordUtil;
import com.banking.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Also demonstrates the SERVLET LIFECYCLE:
 *   init()    - called once when the container loads the servlet
 *   service() - called for every request, dispatches to doGet()/doPost()
 *   destroy() - called once when the servlet is removed / server stops
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        log("LoginServlet.init() called");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        log("LoginServlet.service() called for HTTP " + req.getMethod());
        super.service(req, res);            // super.service() calls doGet() or doPost()
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = (User) (req.getSession(false) == null ? null : req.getSession(false).getAttribute("user"));
        if (user != null) {                  // already logged in
            res.sendRedirect(req.getContextPath() + (user.isAdmin() ? "/admin" : "/dashboard"));
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, res);   // RequestDispatcher
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (Validator.isBlank(username) || Validator.isBlank(password)) {
            fail(req, res, "Please enter your email/username and password.");
            return;
        }
        try {
            User user = userDAO.findByUsername(username.trim().toLowerCase());
            if (user == null || !user.isActive() || !PasswordUtil.verify(password, user.getPasswordHash())) {
                fail(req, res, "Invalid email/username or password.");   // same message for both: no user enumeration
                return;
            }
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();         // new session id after login (prevents session fixation)
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(15 * 60);
            res.sendRedirect(req.getContextPath() + (user.isAdmin() ? "/admin" : "/dashboard"));
        } catch (SQLException e) {
            throw new ServletException("Login failed", e);
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse res, String msg) throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.getRequestDispatcher("/login.jsp").forward(req, res);
    }

    @Override
    public void destroy() {
        log("LoginServlet.destroy() called");
    }
}

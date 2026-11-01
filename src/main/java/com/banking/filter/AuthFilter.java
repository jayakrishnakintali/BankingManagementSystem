package com.banking.filter;

import com.banking.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Protects private pages: must be logged in (HttpSession) and have the right role.
 * Customers -> /dashboard, /deposit, /withdraw, /transfer, /transactions
 * Admin     -> /admin
 */
@WebFilter(urlPatterns = {"/dashboard", "/deposit", "/withdraw", "/transfer", "/transactions", "/admin"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String ctx = req.getContextPath();

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {                       // not logged in
            res.sendRedirect(ctx + "/login");
            return;
        }
        boolean adminPage = "/admin".equals(req.getServletPath());
        if (adminPage && !user.isAdmin()) {       // customer trying to open admin page
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Administrators only");
            return;
        }
        if (!adminPage && user.isAdmin()) {       // admin has no customer account
            res.sendRedirect(ctx + "/admin");
            return;
        }
        // do not let the browser cache private pages (back button after logout)
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        chain.doFilter(request, response);
    }
}

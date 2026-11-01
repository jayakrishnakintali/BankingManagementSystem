package com.banking.util;

import com.banking.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Small helpers shared by the servlets. */
public final class WebUtil {
    private WebUtil() { }

    public static User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute("user");
    }

    /** Turns the stored-procedure status into a "message" (success) or "error" request attribute. */
    public static void report(HttpServletRequest req, String status, String successMessage) {
        if ("SUCCESS".equals(status)) req.setAttribute("message", successMessage);
        else req.setAttribute("error", status.replaceFirst("^ERROR: ", ""));
    }
}

package com.cinema.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Small session helper utilities.
 */
public final class SessionUtils {

    private SessionUtils() {}

    public static boolean isUserConnected(HttpServletRequest req) {
        if (req == null) return false;
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("currentUser") != null;
    }
}

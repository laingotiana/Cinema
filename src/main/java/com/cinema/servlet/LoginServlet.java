package com.cinema.servlet;

import com.cinema.dao.PersonneDAO;
import com.cinema.model.Personne;
import com.cinema.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private PersonneDAO personneDAO;

    @Override
    public void init() {
        personneDAO = new PersonneDAO();
        logger.info("LoginServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si déjà connecté, rediriger vers la page d'accueil
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Pass-through of optional returnTo parameter or session-stored returnTo so the login form can preserve it
        String returnTo = request.getParameter("returnTo");
        if (returnTo == null || returnTo.isBlank()) {
            Object rt = request.getSession(false) != null ? request.getSession(false).getAttribute("returnTo") : null;
            if (rt != null) returnTo = String.valueOf(rt);
        }
        if (returnTo != null && !returnTo.isBlank()) {
            request.setAttribute("returnTo", returnTo);
        }

        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("error", "Veuillez renseigner l'email et le mot de passe.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        try {
            Optional<Personne> opt = personneDAO.findByEmail(email);
            if (opt.isEmpty()) {
                request.setAttribute("error", "Identifiants invalides");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            Personne personne = opt.get();
            boolean ok = PasswordUtil.checkPassword(password, personne.getMotDePasseHash());
            if (!ok) {
                request.setAttribute("error", "Identifiants invalides");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            // Authentification réussie
            HttpSession session = request.getSession(true);
            // Remettre un objet léger ou l'entité complète selon besoin
            session.setAttribute("currentUser", personne);
            logger.info("Utilisateur connecté: {}", personne.getEmail());

            // If a returnTo was provided (from POST field or session), redirect there
            String returnTo = request.getParameter("returnTo");
            if (returnTo == null || returnTo.isBlank()) {
                Object rt = request.getSession(false) != null ? request.getSession(false).getAttribute("returnTo") : null;
                if (rt != null) returnTo = String.valueOf(rt);
            }
            if (returnTo != null && !returnTo.isBlank()) {
                // Basic safety: only allow redirect inside this context
                if (returnTo.startsWith(request.getContextPath())) {
                    // clear session-stored returnTo
                    if (request.getSession(false) != null) request.getSession(false).removeAttribute("returnTo");
                    response.sendRedirect(returnTo);
                    return;
                }
            }

            response.sendRedirect(request.getContextPath() + "/");

        } catch (SQLException e) {
            logger.error("Erreur lors de l'authentification", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }
}

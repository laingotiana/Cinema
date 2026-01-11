package com.cinema.servlet;

import com.cinema.dao.GenreDAO;
import com.cinema.model.Genre;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "GenreAdminServlet", urlPatterns = {"/admin/genres"})
public class GenreAdminServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(GenreAdminServlet.class);
    private GenreDAO genreDAO;

    @Override
    public void init() {
        genreDAO = new GenreDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            if (action != null && action.equals("delete")) {
                String idStr = request.getParameter("id");
                if (idStr != null && !idStr.isBlank()) {
                    try {
                        int id = Integer.parseInt(idStr);
                        genreDAO.delete(id);
                        request.getSession().setAttribute("flashError", "Suppression réussie");
                        response.sendRedirect(request.getContextPath() + "/admin/genres");
                        return;
                    } catch (SQLException | NumberFormatException e) {
                        logger.error("Erreur suppression genre", e);
                        request.getSession().setAttribute("flashError", "Erreur lors de la suppression: " + e.getMessage());
                        response.sendRedirect(request.getContextPath() + "/admin/genres");
                        return;
                    }
                }
            }

            List<Genre> genres = genreDAO.findAll();
            request.setAttribute("genres", genres);
            Object flash = request.getSession().getAttribute("flashError");
            if (flash != null) {
                request.setAttribute("error", String.valueOf(flash));
                request.getSession().removeAttribute("flashError");
            }
            request.getRequestDispatcher("/WEB-INF/views/admin/genres.jsp").forward(request, response);
        } catch (SQLException e) {
            logger.error("Erreur récupération genres", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle create/update from genre form
        String idStr = request.getParameter("id");
        String libelle = request.getParameter("libelle");

        if (libelle == null || libelle.isBlank()) {
            request.setAttribute("error", "Le libellé est requis");
            request.getRequestDispatcher("/WEB-INF/views/admin/genre-form.jsp").forward(request, response);
            return;
        }

        Genre g = new Genre();
        g.setLibelle(libelle.trim());

        try {
            if (idStr != null && !idStr.isBlank()) {
                g.setId(Integer.parseInt(idStr));
                boolean ok = genreDAO.update(g);
                if (!ok) {
                    request.setAttribute("error", "Impossible de mettre à jour le genre (introuvable)");
                    request.setAttribute("genre", g);
                    request.getRequestDispatcher("/WEB-INF/views/admin/genre-form.jsp").forward(request, response);
                    return;
                }
            } else {
                genreDAO.create(g);
            }
            response.sendRedirect(request.getContextPath() + "/admin/genres");
        } catch (SQLException e) {
            logger.error("Erreur sauvegarde genre", e);
            request.setAttribute("error", "Erreur base de données: " + e.getMessage());
            request.setAttribute("genre", g);
            request.getRequestDispatcher("/WEB-INF/views/admin/genre-form.jsp").forward(request, response);
        }
    }
}

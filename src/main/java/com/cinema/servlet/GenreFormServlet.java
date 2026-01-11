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
import java.util.Optional;

@WebServlet(name = "GenreFormServlet", urlPatterns = {"/admin/genre"})
public class GenreFormServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(GenreFormServlet.class);
    private GenreDAO genreDAO;

    @Override
    public void init() {
        genreDAO = new GenreDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String action = "create";
        if (idStr != null && !idStr.isBlank()) {
            action = "update";
            try {
                Optional<Genre> g = genreDAO.findById(Integer.parseInt(idStr));
                if (g.isPresent()) {
                    request.setAttribute("genre", g.get());
                } else {
                    request.setAttribute("error", "Genre introuvable");
                }
            } catch (SQLException e) {
                logger.error("Erreur lecture genre", e);
                request.setAttribute("error", "Erreur base de données");
            }
        }

        request.setAttribute("action", action);
        request.setAttribute("activeMenuItem", "genre");
        request.setAttribute("pageTitle", action.equals("create") ? "Créer un genre" : "Modifier un genre");

        request.getRequestDispatcher("/WEB-INF/views/admin/genre-form.jsp").forward(request, response);
    }
}

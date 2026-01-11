package com.cinema.servlet;

import com.cinema.dao.FilmGenreDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "FilmGenreServlet", urlPatterns = {"/admin/film/genres"})
public class FilmGenreServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FilmGenreServlet.class);
    private FilmGenreDAO filmGenreDAO;

    @Override
    public void init() {
        filmGenreDAO = new FilmGenreDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String filmIdStr = request.getParameter("filmId");
        String genreIdStr = request.getParameter("genreId");

        if (filmIdStr == null || filmIdStr.isBlank() || genreIdStr == null || genreIdStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/films");
            return;
        }

        try {
            Integer filmId = Integer.parseInt(filmIdStr);
            Integer genreId = Integer.parseInt(genreIdStr);

            if ("add".equals(action)) {
                filmGenreDAO.addGenreToFilm(filmId, genreId);
            } else if ("remove".equals(action)) {
                filmGenreDAO.removeGenreFromFilm(filmId, genreId);
            }

            response.sendRedirect(request.getContextPath() + "/admin/film/detail?id=" + filmId);
        } catch (NumberFormatException | SQLException e) {
            logger.error("Erreur association genre-film", e);
            request.getSession().setAttribute("flashError", "Erreur lors de la modification des genres");
            response.sendRedirect(request.getContextPath() + "/admin/films");
        }
    }
}

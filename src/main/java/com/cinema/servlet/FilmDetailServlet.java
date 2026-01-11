package com.cinema.servlet;

import com.cinema.dao.FilmDAO;
import com.cinema.dao.FilmGenreDAO;
import com.cinema.dao.GenreDAO;
import com.cinema.model.Film;
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
import java.util.Optional;

@WebServlet(name = "FilmDetailServlet", urlPatterns = {"/admin/film/detail"})
public class FilmDetailServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FilmDetailServlet.class);
    private FilmDAO filmDAO;
    private FilmGenreDAO filmGenreDAO;
    private GenreDAO genreDAO;

    @Override
    public void init() {
        filmDAO = new FilmDAO();
        filmGenreDAO = new FilmGenreDAO();
        genreDAO = new GenreDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/films");
            return;
        }

        try {
            Optional<Film> f = filmDAO.findById(Integer.parseInt(idStr));
            if (f.isEmpty()) {
                request.getSession().setAttribute("flashError", "Film introuvable");
                response.sendRedirect(request.getContextPath() + "/admin/films");
                return;
            }

            Film film = f.get();
            List<Genre> associated = filmGenreDAO.findGenresForFilm(film.getId());
            List<Genre> all = genreDAO.findAll();

            request.setAttribute("film", film);
            request.setAttribute("associatedGenres", associated);
            request.setAttribute("allGenres", all);

            request.getRequestDispatcher("/WEB-INF/views/admin/film-detail.jsp").forward(request, response);
        } catch (SQLException e) {
            logger.error("Erreur lecture détails film", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

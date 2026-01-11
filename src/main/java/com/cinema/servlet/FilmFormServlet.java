package com.cinema.servlet;

import com.cinema.dao.FilmDAO;
import com.cinema.model.Film;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "FilmFormServlet", urlPatterns = {"/admin/film"})
@MultipartConfig
public class FilmFormServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FilmFormServlet.class);
    private FilmDAO filmDAO;

    @Override
    public void init() {
        filmDAO = new FilmDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // No authentication required to view the film form; leave access open so Add/Edit links open the form directly.

        String idStr = request.getParameter("id");
        String action = "create";
        if (idStr != null && !idStr.isBlank()) {
            action = "update";
            try {
                Optional<Film> f = filmDAO.findById(Integer.parseInt(idStr));
                if (f.isPresent()) {
                    request.setAttribute("film", f.get());
                } else {
                    request.setAttribute("error", "Film introuvable");
                }
            } catch (SQLException e) {
                logger.error("Erreur lecture film", e);
                request.setAttribute("error", "Erreur base de données");
            }
        }

        request.setAttribute("action", action);
        request.setAttribute("activeMenuItem", "film");
        request.setAttribute("pageTitle", action.equals("create") ? "Créer un film" : "Modifier un film");

        request.getRequestDispatcher("/WEB-INF/views/admin/film-form.jsp").forward(request, response);
    }
}

package com.cinema.servlet;

import com.cinema.dao.FilmDAO;
import com.cinema.model.Film;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Simplified CRUD-style servlet for Films (list, delete in GET; create/update in POST)
 * Modeled after the provided CategoryServlet example to keep behavior straightforward.
 */
@WebServlet(name = "FilmServlet", urlPatterns = {"/admin/films"})
public class FilmAdminServlet extends HttpServlet {

    private FilmDAO filmDAO;

    @Override
    public void init() {
        filmDAO = new FilmDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");
            if (action != null && action.equals("delete")) {
                String idStr = req.getParameter("id");
                if (idStr != null && !idStr.isBlank()) {
                    try {
                        int id = Integer.parseInt(idStr);
                        filmDAO.delete(id);
                        req.getSession().setAttribute("flashError", "Suppression réussie");
                        resp.sendRedirect(req.getContextPath() + "/admin/films");
                        return;
                    } catch (NumberFormatException nfe) {
                        req.getSession().setAttribute("flashError", "Identifiant invalide");
                        resp.sendRedirect(req.getContextPath() + "/admin/films");
                        return;
                    } catch (Exception e) {
                        req.getSession().setAttribute("flashError", "Erreur lors de la suppression: " + e.getMessage());
                        resp.sendRedirect(req.getContextPath() + "/admin/films");
                        return;
                    }
                }
            }

            ArrayList<Film> films = new ArrayList<>(filmDAO.findAll());
            req.setAttribute("films", films);
            Object flash = req.getSession().getAttribute("flashError");
            if (flash != null) {
                req.setAttribute("error", String.valueOf(flash));
                req.getSession().removeAttribute("flashError");
            }
            req.setAttribute("activeMenuItem", "film");
            req.setAttribute("pageTitle", "Gestion des films");

            // forward to films JSP
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/films.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String idStr = req.getParameter("id");
        String titre = req.getParameter("titre");
        String description = req.getParameter("description");
        String dureeStr = req.getParameter("duree_minutes");
        String dateSortieStr = req.getParameter("date_sortie");
        String ageMinStr = req.getParameter("age_min");
        String langue = req.getParameter("langue_originale");
        String affiche = req.getParameter("affiche_url");

        Film film = new Film();
        if (idStr != null && !idStr.isBlank()) {
            try {
                film.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException ignored) {
            }
        }

        film.setTitre(titre);
        film.setDescription(description);
        try {
            if (dureeStr != null && !dureeStr.isBlank()) film.setDureeMinutes(Integer.parseInt(dureeStr));
            if (dateSortieStr != null && !dateSortieStr.isBlank()) film.setDateSortie(Date.valueOf(dateSortieStr));
            if (ageMinStr != null && !ageMinStr.isBlank()) film.setAgeMin(Integer.parseInt(ageMinStr));
        } catch (NumberFormatException nfe) {
            req.setAttribute("error", "Certaines valeurs numériques sont invalides (durée, âge).");
            req.setAttribute("film", film);
            req.getRequestDispatcher("/WEB-INF/views/admin/film-form.jsp").forward(req, resp);
            return;
        }
        film.setLangueOriginale(langue);
        film.setAfficheUrl(affiche);

        try {
            if (film.getId() != null && film.getId() > 0) {
                boolean ok = filmDAO.update(film);
                if (!ok) {
                    req.setAttribute("error", "Impossible de mettre à jour le film (introuvable)");
                    req.setAttribute("film", film);
                    req.getRequestDispatcher("/WEB-INF/views/admin/film-form.jsp").forward(req, resp);
                    return;
                }
            } else {
                filmDAO.create(film);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/films");
    }
}

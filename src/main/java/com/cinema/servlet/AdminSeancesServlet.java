package com.cinema.servlet;

import com.cinema.dao.FilmDAO;
import com.cinema.dao.SalleDAO;
import com.cinema.dao.SeanceDAO;
import com.cinema.model.Film;
import com.cinema.model.Salle;
import com.cinema.model.Seance;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "AdminSeancesServlet", urlPatterns = "/admin/seances")
public class AdminSeancesServlet extends HttpServlet {
    private final SeanceDAO seanceDAO = new SeanceDAO();
    private final FilmDAO filmDAO = new FilmDAO();
    private final SalleDAO salleDAO = new SalleDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");
            if (action != null && action.equals("delete")) {
                String idStr = req.getParameter("id");
                if (idStr != null && !idStr.isBlank()) {
                    try {
                        int id = Integer.parseInt(idStr);
                        seanceDAO.delete(id);
                        req.getSession().setAttribute("flashSuccess", "Séance supprimée");
                    } catch (NumberFormatException nfe) {
                        req.getSession().setAttribute("flashError", "Identifiant invalide");
                    } catch (Exception e) {
                        req.getSession().setAttribute("flashError", "Erreur lors de la suppression: " + e.getMessage());
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/admin/seances");
                return;
            }

            List<Seance> seances = seanceDAO.findAll();
            req.setAttribute("seances", seances);
            req.setAttribute("films", filmDAO.findAll());
            req.setAttribute("salles", salleDAO.findAll());

            // flash messages
            Object flashErr = req.getSession().getAttribute("flashError");
            if (flashErr != null) {
                req.setAttribute("error", String.valueOf(flashErr));
                req.getSession().removeAttribute("flashError");
            }
            Object flashSuccess = req.getSession().getAttribute("flashSuccess");
            if (flashSuccess != null) {
                req.setAttribute("success", String.valueOf(flashSuccess));
                req.getSession().removeAttribute("flashSuccess");
            }

            req.getRequestDispatcher("/WEB-INF/views/admin/seances.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String filmId = req.getParameter("id_film");
        String salleId = req.getParameter("id_salle");
        String debutStr = req.getParameter("debut");
        String langue = req.getParameter("langue");

        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        try {
            LocalDateTime debut = LocalDateTime.parse(debutStr, fmt);
            // need film duration to compute fin
            java.util.Optional<Film> fOpt = filmDAO.findById(Integer.parseInt(filmId));
            LocalDateTime fin = debut;
            if (fOpt.isPresent() && fOpt.get().getDureeMinutes() != null) fin = debut.plusMinutes(fOpt.get().getDureeMinutes());

            Integer id = (idStr == null || idStr.isBlank()) ? null : Integer.parseInt(idStr);
            Seance s = new Seance();
            if (id != null) s.setId(id);
            s.setIdFilm(Integer.parseInt(filmId));
            s.setIdSalle(Integer.parseInt(salleId));
            s.setDebut(debut);
            s.setFin(fin);
            s.setLangue(langue);

            // conflict check
            boolean conflict = seanceDAO.hasConflict(s.getIdSalle(), debut, fin, s.getId());
            if (conflict) {
                req.getSession().setAttribute("flashError", "Conflit détecté: il existe déjà une séance pour cette salle à ce créneau.");
                resp.sendRedirect(req.getContextPath() + "/admin/seance" + (id != null ? "?id=" + id : ""));
                return;
            }

            if (id == null) {
                seanceDAO.create(s);
                req.getSession().setAttribute("flashSuccess", "Séance créée");
            } else {
                seanceDAO.update(s);
                req.getSession().setAttribute("flashSuccess", "Séance mise à jour");
            }
            resp.sendRedirect(req.getContextPath() + "/admin/seances");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

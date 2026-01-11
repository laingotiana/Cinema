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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "SeanceFormServlet", urlPatterns = "/admin/seance")
public class SeanceFormServlet extends HttpServlet {
    private final SeanceDAO seanceDAO = new SeanceDAO();
    private final FilmDAO filmDAO = new FilmDAO();
    private final SalleDAO salleDAO = new SalleDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String action = "create";
        try {
            if (idStr != null && !idStr.isBlank()) {
                action = "update";
                Seance s = seanceDAO.findById(Integer.parseInt(idStr));
                if (s != null) {
                    req.setAttribute("seance", s);
                    if (s.getDebut() != null) {
                        req.setAttribute("debutLocal", s.getDebut().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }
                } else {
                    req.setAttribute("error", "Séance introuvable");
                }
            }

            // flash messages from session
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

            req.setAttribute("films", filmDAO.findAll());
            req.setAttribute("salles", salleDAO.findAll());
            req.setAttribute("action", action);
            req.setAttribute("activeMenuItem", "seance");
            req.setAttribute("pageTitle", action.equals("create") ? "Créer une séance" : "Modifier une séance");

            req.getRequestDispatcher("/WEB-INF/views/admin/seance-form.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

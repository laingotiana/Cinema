package com.cinema.servlet;

import com.cinema.dao.SalleDAO;
import com.cinema.model.Salle;
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

@WebServlet(name = "SalleAdminServlet", urlPatterns = {"/admin/salles"})
public class SalleAdminServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SalleAdminServlet.class);
    private SalleDAO salleDAO;

    @Override
    public void init() {
        salleDAO = new SalleDAO();
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
                        salleDAO.delete(id);
                        request.getSession().setAttribute("flashError", "Suppression réussie");
                    } catch (NumberFormatException nfe) {
                        request.getSession().setAttribute("flashError", "Identifiant invalide");
                    } catch (Exception e) {
                        request.getSession().setAttribute("flashError", "Erreur lors de la suppression: " + e.getMessage());
                    }
                }
                response.sendRedirect(request.getContextPath() + "/admin/salles");
                return;
            }

            List<Salle> salles = salleDAO.findAll();
            request.setAttribute("salles", salles);
            Object flash = request.getSession().getAttribute("flashError");
            if (flash != null) {
                request.setAttribute("error", String.valueOf(flash));
                request.getSession().removeAttribute("flashError");
            }
            request.getRequestDispatcher("/WEB-INF/views/admin/salles.jsp").forward(request, response);
        } catch (SQLException e) {
            logger.error("Erreur récupération salles", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String nom = request.getParameter("nom");
        String capStr = request.getParameter("capacite");

        if (nom == null || nom.isBlank()) {
            request.setAttribute("error", "Le nom de la salle est requis");
            request.getRequestDispatcher("/WEB-INF/views/admin/salle-form.jsp").forward(request, response);
            return;
        }

        Integer capacite = null;
        try {
            if (capStr != null && !capStr.isBlank()) {
                capacite = Integer.parseInt(capStr);
                if (capacite <= 0) throw new NumberFormatException("capacité doit être > 0");
            }
        } catch (NumberFormatException nfe) {
            request.setAttribute("error", "La capacité doit être un nombre entier positif");
            request.getRequestDispatcher("/WEB-INF/views/admin/salle-form.jsp").forward(request, response);
            return;
        }

        Salle s = new Salle();
        s.setNom(nom.trim());
        s.setCapacite(capacite);

        try {
            if (idStr != null && !idStr.isBlank()) {
                s.setId(Integer.parseInt(idStr));
                boolean ok = salleDAO.update(s);
                if (!ok) {
                    request.setAttribute("error", "Impossible de mettre à jour la salle (introuvable)");
                    request.setAttribute("salle", s);
                    request.getRequestDispatcher("/WEB-INF/views/admin/salle-form.jsp").forward(request, response);
                    return;
                }
            } else {
                salleDAO.create(s);
            }
            response.sendRedirect(request.getContextPath() + "/admin/salles");
        } catch (SQLException e) {
            logger.error("Erreur sauvegarde salle", e);
            request.setAttribute("error", "Erreur base de données: " + e.getMessage());
            request.setAttribute("salle", s);
            request.getRequestDispatcher("/WEB-INF/views/admin/salle-form.jsp").forward(request, response);
        }
    }
}

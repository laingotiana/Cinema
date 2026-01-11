package com.cinema.servlet;

import com.cinema.dao.TarifSeanceDAO;
import com.cinema.model.TarifSeance;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(name = "TarifSeanceFormServlet", urlPatterns = {"/admin/tarif-seance"})
public class TarifSeanceFormServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TarifSeanceFormServlet.class);
    private TarifSeanceDAO tarifSeanceDAO;

    @Override
    public void init() { tarifSeanceDAO = new TarifSeanceDAO(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // load auxiliary lists: seances, types, categories
        try {
            com.cinema.dao.SeanceDAO seanceDAO = new com.cinema.dao.SeanceDAO();
            com.cinema.dao.TypePlaceDAO typeDAO = new com.cinema.dao.TypePlaceDAO();
            com.cinema.dao.CategoriePersonneDAO catDAO = new com.cinema.dao.CategoriePersonneDAO();
            req.setAttribute("seances", seanceDAO.findAll());
            req.setAttribute("types", typeDAO.findAll());
            req.setAttribute("categories", catDAO.findAll());
        } catch (SQLException e) {
            logger.error("Erreur chargement données pour tarif séance", e);
            req.setAttribute("error", "Erreur base de données: " + e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/tarif-seance-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String seanceStr = req.getParameter("id_seance");
        String typeStr = req.getParameter("id_type_place");
        String catStr = req.getParameter("id_categorie_personne");
        String prixStr = req.getParameter("prix");

        if (seanceStr == null || typeStr == null || catStr == null || prixStr == null) {
            req.setAttribute("error", "Tous les champs sont requis");
            req.getRequestDispatcher("/WEB-INF/views/admin/tarif-seance-form.jsp").forward(req, resp);
            return;
        }

        try {
            TarifSeance t = new TarifSeance();
            t.setIdSeance(Integer.parseInt(seanceStr));
            t.setIdTypePlace(Integer.parseInt(typeStr));
            t.setIdCategoriePersonne(Integer.parseInt(catStr));
            t.setPrix(new BigDecimal(prixStr));

            tarifSeanceDAO.create(t);
            resp.sendRedirect(req.getContextPath() + "/admin/tarifs");
        } catch (SQLException e) {
            logger.error("Erreur sauvegarde tarif seance", e);
            req.setAttribute("error", "Erreur base de données: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/tarif-seance-form.jsp").forward(req, resp);
        } catch (NumberFormatException ex) {
            req.setAttribute("error", "Format invalide");
            req.getRequestDispatcher("/WEB-INF/views/admin/tarif-seance-form.jsp").forward(req, resp);
        }
    }
}

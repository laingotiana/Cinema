package com.cinema.servlet;

import com.cinema.dao.TarifDefautDAO;
import com.cinema.model.TarifDefaut;
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

@WebServlet(name = "TarifFormServlet", urlPatterns = {"/admin/tarif"})
public class TarifFormServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TarifFormServlet.class);
    private TarifDefautDAO tarifDefautDAO;
    private com.cinema.dao.TypePlaceDAO typePlaceDAO;
    private com.cinema.dao.CategoriePersonneDAO categorieDAO;

    @Override
    public void init() { 
        tarifDefautDAO = new TarifDefautDAO(); 
        typePlaceDAO = new com.cinema.dao.TypePlaceDAO();
        categorieDAO = new com.cinema.dao.CategoriePersonneDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String action = "create";
        if (idStr != null && !idStr.isBlank()) {
            action = "update";
            try {
                TarifDefaut t = tarifDefautDAO.findById(Integer.parseInt(idStr));
                if (t != null) req.setAttribute("tarif", t);
                else req.setAttribute("error", "Tarif introuvable");
            } catch (SQLException e) {
                logger.error("Erreur lecture tarif", e);
                req.setAttribute("error", "Erreur base de données");
            }
        }
        try {
            req.setAttribute("types", typePlaceDAO.findAll());
            req.setAttribute("categories", categorieDAO.findAll());
        } catch (SQLException e) {
            logger.error("Erreur lecture données auxiliaires", e);
        }

        Object flash = req.getSession().getAttribute("flashError");
        if (flash != null) {
            req.setAttribute("error", String.valueOf(flash));
            req.getSession().removeAttribute("flashError");
        }
        Object flashSuccess = req.getSession().getAttribute("flashSuccess");
        if (flashSuccess != null) {
            req.setAttribute("success", String.valueOf(flashSuccess));
            req.getSession().removeAttribute("flashSuccess");
        }

        req.setAttribute("action", action);
        req.setAttribute("activeMenuItem", "tarif");
        req.setAttribute("pageTitle", action.equals("create") ? "Créer un tarif" : "Modifier un tarif");

        req.getRequestDispatcher("/WEB-INF/views/admin/tarif-form.jsp").forward(req, resp);
    }
}

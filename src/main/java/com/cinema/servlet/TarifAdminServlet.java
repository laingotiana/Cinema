package com.cinema.servlet;

import com.cinema.dao.CategoriePersonneDAO;
import com.cinema.dao.TarifDefautDAO;
import com.cinema.dao.TarifSeanceDAO;
import com.cinema.dao.TypePlaceDAO;
import com.cinema.model.TarifDefaut;
import com.cinema.model.TarifSeance;
import com.cinema.model.CategoriePersonne;
import com.cinema.model.TypePlace;
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

@WebServlet(name = "TarifAdminServlet", urlPatterns = {"/admin/tarifs"})
public class TarifAdminServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TarifAdminServlet.class);
    private TarifDefautDAO tarifDefautDAO;
    private TarifSeanceDAO tarifSeanceDAO;
    private TypePlaceDAO typePlaceDAO;
    private CategoriePersonneDAO categorieDAO;

    @Override
    public void init() {
        tarifDefautDAO = new TarifDefautDAO();
        tarifSeanceDAO = new TarifSeanceDAO();
        typePlaceDAO = new TypePlaceDAO();
        categorieDAO = new CategoriePersonneDAO();
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
                        tarifDefautDAO.delete(id);
                        req.getSession().setAttribute("flashSuccess", "Suppression réussie");
                    } catch (NumberFormatException nfe) {
                        req.getSession().setAttribute("flashError", "Identifiant invalide");
                    } catch (Exception e) {
                        req.getSession().setAttribute("flashError", "Erreur lors de la suppression: " + e.getMessage());
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/admin/tarifs");
                return;
            }

            List<TarifDefaut> defauts = tarifDefautDAO.findAll();
            List<TarifSeance> seanceTarifs = tarifSeanceDAO.findAll();
            List<TypePlace> types = typePlaceDAO.findAll();
            List<CategoriePersonne> categories = categorieDAO.findAll();

            req.setAttribute("tarifsDefaut", defauts);
            req.setAttribute("tarifsSeance", seanceTarifs);
            req.setAttribute("types", types);
            req.setAttribute("categories", categories);

            Object flash = req.getSession().getAttribute("flashError");
            if (flash != null) { req.setAttribute("error", String.valueOf(flash)); req.getSession().removeAttribute("flashError"); }
            Object flashSuccess = req.getSession().getAttribute("flashSuccess");
            if (flashSuccess != null) { req.setAttribute("success", String.valueOf(flashSuccess)); req.getSession().removeAttribute("flashSuccess"); }

            req.getRequestDispatcher("/WEB-INF/views/admin/tarifs.jsp").forward(req, resp);
        } catch (SQLException e) {
            logger.error("Erreur récupération tarifs", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String typeStr = req.getParameter("id_type_place");
        String catStr = req.getParameter("id_categorie_personne");
        String prixStr = req.getParameter("prix");

        if (typeStr == null || catStr == null || prixStr == null) {
            req.setAttribute("error", "Tous les champs sont requis");
            req.getRequestDispatcher("/WEB-INF/views/admin/tarif-form.jsp").forward(req, resp);
            return;
        }

        try {
            TarifDefaut t = new TarifDefaut();
            if (idStr != null && !idStr.isBlank()) t.setId(Integer.parseInt(idStr));
            t.setIdTypePlace(Integer.parseInt(typeStr));
            t.setIdCategoriePersonne(Integer.parseInt(catStr));
            t.setPrix(new java.math.BigDecimal(prixStr));

            if (t.getId() != null) {
                boolean ok = tarifDefautDAO.update(t);
                if (!ok) { req.setAttribute("error", "Impossible de mettre à jour"); req.setAttribute("tarif", t); req.getRequestDispatcher("/WEB-INF/views/admin/tarif-form.jsp").forward(req, resp); return; }
            } else {
                tarifDefautDAO.create(t);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/tarifs");
        } catch (SQLException e) {
            logger.error("Erreur sauvegarde tarif", e);
            req.setAttribute("error", "Erreur base de données: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/tarif-form.jsp").forward(req, resp);
        } catch (NumberFormatException ex) {
            req.setAttribute("error", "Format invalide");
            req.getRequestDispatcher("/WEB-INF/views/admin/tarif-form.jsp").forward(req, resp);
        }
    }
}

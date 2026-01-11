package com.cinema.servlet;

import com.cinema.dao.TicketDAO;
import com.cinema.dao.PricingDAO;
import com.cinema.model.Ticket;
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

@WebServlet(name = "TicketFormServlet", urlPatterns = {"/admin/ticket"})
public class TicketFormServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TicketFormServlet.class);
    private TicketDAO ticketDAO;
    private PricingDAO pricingDAO;

    @Override public void init() { ticketDAO = new TicketDAO(); pricingDAO = new PricingDAO(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/admin/ticket-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String seanceStr = req.getParameter("id_seance");
        String placeStr = req.getParameter("id_place");
        String categorieStr = req.getParameter("id_categorie_personne");

        if (seanceStr == null || placeStr == null || categorieStr == null) {
            req.setAttribute("error", "Champs requis manquants");
            req.getRequestDispatcher("/WEB-INF/views/admin/ticket-form.jsp").forward(req, resp);
            return;
        }

        try {
            int idSeance = Integer.parseInt(seanceStr);
            int idPlace = Integer.parseInt(placeStr);
            int idCategorie = Integer.parseInt(categorieStr);

            int idTypePlace = 1;
            try {
                try (java.sql.Connection conn = com.cinema.util.DatabaseConnection.getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id_type_place FROM place WHERE id = ?")) {
                    ps.setInt(1, idPlace);
                    try (java.sql.ResultSet rs = ps.executeQuery()) { if (rs.next()) idTypePlace = rs.getInt(1); }
                }
                BigDecimal prix = pricingDAO.calculerPrixTicket(idSeance, idTypePlace, idCategorie);

                Ticket t = new Ticket();
                t.setIdReservation(null);
                t.setIdSeance(idSeance);
                t.setIdPlace(idPlace);
                t.setIdStatut(getDefaultTicketStatutId());
                t.setIdCategoriePersonne(idCategorie);
                t.setPrix(prix);
                t.setCodeBarre(java.util.UUID.randomUUID().toString());

                try {
                    ticketDAO.create(t);
                    resp.sendRedirect(req.getContextPath() + "/admin/ticket/detail?id=" + t.getId());
                    return;
                } catch (SQLException ex) {
                    logger.error("Erreur création ticket vente sur place", ex);
                    req.setAttribute("error", "Impossible de créer le ticket: " + ex.getMessage());
                    req.getRequestDispatcher("/WEB-INF/views/admin/ticket-form.jsp").forward(req, resp);
                    return;
                }
            } catch (SQLException sqlEx) {
                logger.error("Erreur lecture place ou calcul prix", sqlEx);
                req.setAttribute("error", "Erreur base de données: " + sqlEx.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/admin/ticket-form.jsp").forward(req, resp);
                return;
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Format invalide");
            req.getRequestDispatcher("/WEB-INF/views/admin/ticket-form.jsp").forward(req, resp);
            return;
        }
    }

    private int getDefaultTicketStatutId() {
        try (java.sql.Connection conn = com.cinema.util.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id FROM statut_ticket WHERE code = 'RESERVE'")) {
            try (java.sql.ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { /* ignore */ }
        return 1;
    }
}

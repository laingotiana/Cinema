package com.cinema.servlet;

import com.cinema.dao.ReservationDAO;
import com.cinema.dao.TicketDAO;
import com.cinema.dao.PricingDAO;
import com.cinema.model.Reservation;
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

@WebServlet(name = "ReservationFormServlet", urlPatterns = {"/admin/reservation"})
public class ReservationFormServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReservationFormServlet.class);
    private ReservationDAO reservationDAO;

    @Override public void init() { reservationDAO = new ReservationDAO(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isBlank()) {
            try {
                Reservation r = reservationDAO.findById(Integer.parseInt(idStr));
                req.setAttribute("reservation", r);
            } catch (SQLException e) {
                logger.error("Erreur lecture réservation", e);
                req.setAttribute("error", "Erreur base de données: " + e.getMessage());
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/reservation-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String personneStr = req.getParameter("id_personne");
        String seanceStr = req.getParameter("id_seance");
        String statutStr = req.getParameter("id_statut");

        if (seanceStr == null || seanceStr.isBlank()) {
            req.setAttribute("error", "La séance est requise");
            req.getRequestDispatcher("/WEB-INF/views/admin/reservation-form.jsp").forward(req, resp);
            return;
        }

        TicketDAO ticketDAO = new TicketDAO();
        PricingDAO pricingDAO = new PricingDAO();

        try {
            Reservation r = new Reservation();
            if (personneStr != null && !personneStr.isBlank()) r.setIdPersonne(Integer.parseInt(personneStr));
            r.setIdSeance(Integer.parseInt(seanceStr));
            if (statutStr != null && !statutStr.isBlank()) r.setIdStatut(Integer.parseInt(statutStr));
            r.setMontantTotal(BigDecimal.ZERO);
            reservationDAO.create(r);

            // Optionally create multiple tickets supplied in the form as id_place[] and id_categorie_personne[]
            String[] placeIds = req.getParameterValues("id_place");
            String[] categories = req.getParameterValues("id_categorie_personne");
            BigDecimal total = BigDecimal.ZERO;
            if (placeIds != null && placeIds.length > 0) {
                for (int i = 0; i < placeIds.length; i++) {
                    try {
                        int idPlace = Integer.parseInt(placeIds[i]);
                        int idCategorie = (categories != null && categories.length > i && categories[i] != null && !categories[i].isBlank()) ? Integer.parseInt(categories[i]) : 1;
                        // lookup place's type
                        int idTypePlace = 1;
                        try (java.sql.Connection conn = com.cinema.util.DatabaseConnection.getConnection();
                             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id_type_place FROM place WHERE id = ?")) {
                            ps.setInt(1, idPlace);
                            try (java.sql.ResultSet rs = ps.executeQuery()) { if (rs.next()) idTypePlace = rs.getInt(1); }
                        }
                        java.math.BigDecimal prix = pricingDAO.calculerPrixTicket(r.getIdSeance(), idTypePlace, idCategorie);
                        Ticket t = new Ticket();
                        t.setIdReservation(r.getId());
                        t.setIdSeance(r.getIdSeance());
                        t.setIdPlace(idPlace);
                        t.setIdStatut(getDefaultTicketStatutId());
                        t.setIdCategoriePersonne(idCategorie);
                        t.setPrix(prix);
                        t.setCodeBarre(java.util.UUID.randomUUID().toString());
                        try {
                            ticketDAO.create(t);
                            total = total.add(prix);
                        } catch (SQLException ex) {
                            // handle duplicate booking (unique constraint on id_seance,id_place)
                            logger.warn("Impossible de créer le ticket pour place {}: {}", idPlace, ex.getMessage());
                            // continue but do not add to total
                            req.getSession().setAttribute("flashError", "Erreur création ticket pour place " + idPlace + ": " + ex.getMessage());
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            // update reservation total
            if (total.compareTo(BigDecimal.ZERO) > 0) reservationDAO.updateMontantTotal(r.getId(), total);

            resp.sendRedirect(req.getContextPath() + "/admin/reservation/detail?id=" + r.getId());
        } catch (SQLException e) {
            logger.error("Erreur création réservation", e);
            req.setAttribute("error", "Erreur base de données: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/reservation-form.jsp").forward(req, resp);
        }
    }

    private int getDefaultTicketStatutId() {
        // Attempt to fetch statut_ticket id for code 'RESERVE'
        try (java.sql.Connection conn = com.cinema.util.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id FROM statut_ticket WHERE code = 'RESERVE'")) {
            try (java.sql.ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { /* ignore */ }
        return 1; // fallback
    }
}

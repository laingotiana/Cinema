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
import java.util.List;

@WebServlet(name = "ReservationDetailServlet", urlPatterns = {"/admin/reservation/detail"})
public class ReservationDetailServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReservationDetailServlet.class);
    private ReservationDAO reservationDAO;
    private TicketDAO ticketDAO;
    private PricingDAO pricingDAO;

    @Override public void init() { reservationDAO = new ReservationDAO(); ticketDAO = new TicketDAO(); pricingDAO = new PricingDAO(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) { resp.sendRedirect(req.getContextPath() + "/admin/reservations"); return; }
        try {
            int id = Integer.parseInt(idStr);
            Reservation r = reservationDAO.findById(id);
            List<Ticket> tickets = ticketDAO.findByReservation(id);
            req.setAttribute("reservation", r);
            req.setAttribute("tickets", tickets);
            req.setAttribute("history", reservationDAO.getHistory(id));
            req.getRequestDispatcher("/WEB-INF/views/admin/reservation-detail.jsp").forward(req, resp);
        } catch (SQLException e) {
            logger.error("Erreur detail réservation", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // POST used to add a ticket to reservation
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("addTicket".equals(action)) {
            handleAddTicket(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/reservations");
    }

    private void handleAddTicket(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reservationStr = req.getParameter("id_reservation");
        String placeStr = req.getParameter("id_place");
        String categorieStr = req.getParameter("id_categorie_personne");
        if (reservationStr == null || placeStr == null || categorieStr == null) { req.setAttribute("error","Champs manquants"); doGet(req, resp); return; }
        try {
            int idReservation = Integer.parseInt(reservationStr);
            int idPlace = Integer.parseInt(placeStr);
            Reservation r = reservationDAO.findById(idReservation);
            if (r == null) { req.setAttribute("error","Réservation introuvable"); doGet(req, resp); return; }
            int idSeance = r.getIdSeance();
            int idCategorie = Integer.parseInt(categorieStr);
            // determine type_place for the place
            java.sql.Connection conn = com.cinema.util.DatabaseConnection.getConnection();
            int idTypePlace = 1;
            try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id_type_place FROM place WHERE id = ?")) {
                ps.setInt(1, idPlace);
                try (java.sql.ResultSet rs = ps.executeQuery()) { if (rs.next()) idTypePlace = rs.getInt(1); }
            }
            BigDecimal prix = pricingDAO.calculerPrixTicket(idSeance, idTypePlace, idCategorie);
            Ticket t = new Ticket();
            t.setIdReservation(idReservation);
            t.setIdSeance(idSeance);
            t.setIdPlace(idPlace);
            t.setIdStatut(1); // default statut RESERVE id 1? rely on DB values; ideally look up
            t.setIdCategoriePersonne(idCategorie);
            t.setPrix(prix);
            t.setCodeBarre(java.util.UUID.randomUUID().toString());
            ticketDAO.create(t);
            // update reservation total
            BigDecimal total = ticketDAO.sumPrixByReservation(idReservation);
            reservationDAO.updateMontantTotal(idReservation, total);
            resp.sendRedirect(req.getContextPath() + "/admin/reservation/detail?id=" + idReservation);
        } catch (SQLException e) {
            logger.error("Erreur ajout ticket", e);
            req.setAttribute("error", "Erreur base de données: " + e.getMessage());
            doGet(req, resp);
        }
    }
}

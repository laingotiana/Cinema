package com.cinema.servlet;

import com.cinema.dao.TicketDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "TicketStatusServlet", urlPatterns = {"/admin/ticket/status"})
public class TicketStatusServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TicketStatusServlet.class);
    private TicketDAO ticketDAO;

    @Override public void init() { ticketDAO = new TicketDAO(); }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ticketStr = req.getParameter("ticket_id");
        String statutStr = req.getParameter("id_statut");
        if (ticketStr == null || statutStr == null) { resp.sendRedirect(req.getContextPath() + "/admin/reservations"); return; }
        try {
            int ticketId = Integer.parseInt(ticketStr);
            int statutId = Integer.parseInt(statutStr);
            ticketDAO.updateStatut(ticketId, statutId, null);
            // update reservation total if linked
            com.cinema.model.Ticket t = ticketDAO.findById(ticketId);
            if (t != null && t.getIdReservation() != null) {
                java.math.BigDecimal total = ticketDAO.sumPrixByReservation(t.getIdReservation());
                new com.cinema.dao.ReservationDAO().updateMontantTotal(t.getIdReservation(), total);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/ticket/detail?id=" + ticketId);
        } catch (NumberFormatException | SQLException e) {
            logger.error("Erreur changement statut ticket", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

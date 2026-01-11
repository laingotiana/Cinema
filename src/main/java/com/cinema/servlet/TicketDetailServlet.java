package com.cinema.servlet;

import com.cinema.dao.TicketDAO;
import com.cinema.model.Ticket;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "TicketDetailServlet", urlPatterns = {"/admin/ticket/detail"})
public class TicketDetailServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TicketDetailServlet.class);
    private TicketDAO ticketDAO;

    @Override public void init() { ticketDAO = new TicketDAO(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) { resp.sendRedirect(req.getContextPath() + "/admin/reservations"); return; }
        try {
            int id = Integer.parseInt(idStr);
            Ticket t = ticketDAO.findById(id);
            if (t == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            req.setAttribute("ticket", t);
            req.setAttribute("history", ticketDAO.getHistory(t.getId()));
            req.getRequestDispatcher("/WEB-INF/views/admin/ticket-detail.jsp").forward(req, resp);
        } catch (SQLException e) {
            logger.error("Erreur detail ticket", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

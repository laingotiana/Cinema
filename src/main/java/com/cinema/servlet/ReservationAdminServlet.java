package com.cinema.servlet;

import com.cinema.dao.ReservationDAO;
import com.cinema.model.Reservation;
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

@WebServlet(name = "ReservationAdminServlet", urlPatterns = {"/admin/reservations"})
public class ReservationAdminServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReservationAdminServlet.class);
    private ReservationDAO reservationDAO;

    @Override public void init() { reservationDAO = new ReservationDAO(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Reservation> list = reservationDAO.findAll();
            req.setAttribute("reservations", list);
            req.getRequestDispatcher("/WEB-INF/views/admin/reservations.jsp").forward(req, resp);
        } catch (SQLException e) {
            logger.error("Erreur récupération réservations", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

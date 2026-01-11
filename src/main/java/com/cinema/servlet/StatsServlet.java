package com.cinema.servlet;

import com.cinema.dao.StatsDAO;
import com.cinema.model.stats.FilmRevenue;
import com.cinema.model.stats.SeanceStat;
import com.cinema.model.stats.SalleOccupancy;
import com.cinema.model.stats.TicketHistory;
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

@WebServlet(name = "StatsServlet", urlPatterns = {"/admin/stats"})
public class StatsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(StatsServlet.class);
    private StatsDAO statsDAO;

    @Override
    public void init() {
        statsDAO = new StatsDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<SeanceStat> seanceStats = statsDAO.ticketsAndRevenueBySeance();
            List<FilmRevenue> filmRevenue = statsDAO.revenueByFilm();
            List<SalleOccupancy> occupancy = statsDAO.occupancyBySalle();
            List<TicketHistory> history = statsDAO.recentSalesHistory(200);

            request.setAttribute("seanceStats", seanceStats);
            request.setAttribute("filmRevenue", filmRevenue);
            request.setAttribute("occupancy", occupancy);
            request.setAttribute("history", history);

            request.getRequestDispatcher("/WEB-INF/views/admin/stats.jsp").forward(request, response);
        } catch (SQLException e) {
            logger.error("Erreur génération statistiques", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

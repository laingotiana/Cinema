package com.cinema.servlet;

import com.cinema.dao.PlaceDAO;
import com.cinema.dao.TypePlaceDAO;
import com.cinema.model.Place;
import com.cinema.model.TypePlace;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "PlaceListServlet", urlPatterns = "/admin/places")
public class PlaceListServlet extends HttpServlet {
    private final PlaceDAO placeDAO = new PlaceDAO();
    private final TypePlaceDAO typePlaceDAO = new TypePlaceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String salleIdStr = req.getParameter("salleId");
        if (salleIdStr == null || salleIdStr.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/admin/salles");
            return;
        }
        int salleId = Integer.parseInt(salleIdStr);
        try {
            List<Place> places = placeDAO.findBySalleId(salleId);
            req.setAttribute("places", places);
            req.setAttribute("salleId", salleId);
            req.setAttribute("types", typePlaceDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/places.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

package com.cinema.servlet;

import com.cinema.dao.PlaceDAO;
import com.cinema.dao.TypePlaceDAO;
import com.cinema.model.Place;
import com.cinema.model.TypePlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PlaceGenerateServlet", urlPatterns = "/admin/places/generate")
public class PlaceGenerateServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PlaceGenerateServlet.class);

    private final TypePlaceDAO typePlaceDAO = new TypePlaceDAO();
    private final PlaceDAO placeDAO = new PlaceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String salleId = req.getParameter("salleId");
        req.setAttribute("salleId", salleId);
        try {
            List<TypePlace> types = typePlaceDAO.findAll();
            req.setAttribute("types", types);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/place-generate.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String salleIdStr = req.getParameter("salleId");
        String rows = req.getParameter("rows");
        String cols = req.getParameter("cols");
        String startNumber = req.getParameter("startNumber");
        String typeIdStr = req.getParameter("typeId");

        int salleId = Integer.parseInt(salleIdStr);
        int r = Integer.parseInt(rows);
        int c = Integer.parseInt(cols);
        int start = (startNumber == null || startNumber.isBlank()) ? 1 : Integer.parseInt(startNumber);
        Integer typeId = (typeIdStr == null || typeIdStr.isBlank()) ? null : Integer.parseInt(typeIdStr);

        // Build places list
        List<Place> toCreate = new ArrayList<>();
        for (int i = 0; i < r; i++) {
            char rowChar = (char) ('A' + i);
            String rangee = String.valueOf(rowChar);
            for (int j = 0; j < c; j++) {
                int numero = start + j;
                Place p = new Place();
                p.setIdSalle(salleId);
                p.setRangee(rangee);
                p.setNumero(numero);
                p.setCodePlace(rangee + String.format("%02d", numero));
                p.setIdTypePlace(typeId);
                toCreate.add(p);
            }
        }

        int created = 0;
        try {
            created = placeDAO.bulkCreateForSalle(salleId, toCreate);
            req.getSession().setAttribute("flash_success", created + " place(s) créées. (les doublons existants ont été ignorés)");
        } catch (Exception e) {
            logger.error("Erreur génération places", e);
            req.getSession().setAttribute("flash_error", "Erreur lors de la génération des places: " + e.getMessage());
        }
        // Redirect to the places list for the salle
        resp.sendRedirect(req.getContextPath() + "/admin/places?salleId=" + salleId);
    }
}

package com.cinema.servlet;

import com.cinema.dao.SalleDAO;
import com.cinema.model.Salle;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "SalleFormServlet", urlPatterns = {"/admin/salle"})
public class SalleFormServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SalleFormServlet.class);
    private SalleDAO salleDAO;

    @Override
    public void init() {
        salleDAO = new SalleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String action = "create";
        if (idStr != null && !idStr.isBlank()) {
            action = "update";
            try {
                Optional<Salle> s = salleDAO.findById(Integer.parseInt(idStr));
                if (s.isPresent()) {
                    request.setAttribute("salle", s.get());
                } else {
                    request.setAttribute("error", "Salle introuvable");
                }
            } catch (SQLException e) {
                logger.error("Erreur lecture salle", e);
                request.setAttribute("error", "Erreur base de données");
            }
        }

        request.setAttribute("action", action);
        request.setAttribute("activeMenuItem", "salle");
        request.setAttribute("pageTitle", action.equals("create") ? "Créer une salle" : "Modifier une salle");

        request.getRequestDispatcher("/WEB-INF/views/admin/salle-form.jsp").forward(request, response);
    }
}

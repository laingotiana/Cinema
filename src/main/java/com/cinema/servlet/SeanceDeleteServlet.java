package com.cinema.servlet;

import com.cinema.dao.SeanceDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "SeanceDeleteServlet", urlPatterns = "/admin/seance/delete")
public class SeanceDeleteServlet extends HttpServlet {
    private final SeanceDAO seanceDAO = new SeanceDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/seances");
            return;
        }
        int id = Integer.parseInt(idStr);
        try {
            boolean ok = seanceDAO.delete(id);
            if (ok) req.getSession().setAttribute("flash_success", "Séance supprimée");
            else req.getSession().setAttribute("flash_error", "Impossible de supprimer la séance");
            resp.sendRedirect(req.getContextPath() + "/admin/seances");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

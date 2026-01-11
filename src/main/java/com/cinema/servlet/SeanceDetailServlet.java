package com.cinema.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cinema.util.DatabaseConnection;

@WebServlet(name = "SeanceDetailServlet", urlPatterns = "/seance/places")
public class SeanceDetailServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id_seance");
        if (idStr == null) { resp.setStatus(400); resp.getWriter().write("{\"error\":\"missing id_seance\"}"); return; }
        int id = Integer.parseInt(idStr);
    String sql = "SELECT p.id AS id_place, p.code_place, p.rangee, p.numero, p.id_type_place, tp.libelle AS type_place, " +
        "CASE WHEN t.id IS NULL THEN true WHEN st.code IN ('ANNULE','REMBOURSE') THEN true ELSE false END AS disponible " +
        "FROM seance s JOIN place p ON p.id_salle = s.id_salle " +
        "LEFT JOIN type_place tp ON p.id_type_place = tp.id " +
        "LEFT JOIN ticket t ON t.id_seance = s.id AND t.id_place = p.id " +
        "LEFT JOIN statut_ticket st ON t.id_statut = st.id " +
        "WHERE s.id = ? ORDER BY p.rangee, p.numero";
        List<Map<String, Object>> places = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("idPlace", rs.getInt("id_place"));
                    p.put("code", rs.getString("code_place"));
                    p.put("rangee", rs.getString("rangee"));
                    p.put("numero", rs.getObject("numero"));
                    p.put("type", rs.getString("type_place"));
                    p.put("idTypePlace", rs.getObject("id_type_place") != null ? rs.getInt("id_type_place") : null);
                    p.put("disponible", rs.getBoolean("disponible"));
                    places.add(p);
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(places));
    }
}

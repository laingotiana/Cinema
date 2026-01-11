package com.cinema.dao;

import com.cinema.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple wrapper to call DB function calculer_prix_ticket
 */
public class PricingDAO {

    public BigDecimal calculerPrixTicket(int idSeance, int idTypePlace, int idCategoriePersonne) throws SQLException {
        String sql = "SELECT calculer_prix_ticket(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSeance);
            ps.setInt(2, idTypePlace);
            ps.setInt(3, idCategoriePersonne);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }
}

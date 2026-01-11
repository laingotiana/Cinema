package com.cinema.dao;

import com.cinema.model.Place;
import com.cinema.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlaceDAO {
    private static final Logger logger = LoggerFactory.getLogger(PlaceDAO.class);

    public List<Place> findBySalleId(Integer salleId) throws SQLException {
        List<Place> list = new ArrayList<>();
        String sql = "SELECT p.*, tp.libelle as type_libelle FROM place p LEFT JOIN type_place tp ON p.id_type_place = tp.id WHERE p.id_salle = ? ORDER BY p.rangee, p.numero";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, salleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public Place create(Place p) throws SQLException {
        String sql = "INSERT INTO place (id_salle, rangee, numero, code_place, id_type_place) VALUES (?, ?, ?, ?, ?) RETURNING id, cree_le";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdSalle());
            ps.setString(2, p.getRangee());
            ps.setObject(3, p.getNumero());
            ps.setString(4, p.getCodePlace());
            if (p.getIdTypePlace() != null) ps.setInt(5, p.getIdTypePlace()); else ps.setNull(5, Types.INTEGER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.setId(rs.getInt("id"));
                    Object createdObj = rs.getObject("cree_le");
                    if (createdObj instanceof LocalDateTime) p.setCreeLe((LocalDateTime) createdObj);
                    else if (createdObj instanceof OffsetDateTime) p.setCreeLe(((OffsetDateTime) createdObj).toLocalDateTime());
                    else {
                        Timestamp ts = rs.getTimestamp("cree_le");
                        if (ts != null) p.setCreeLe(ts.toLocalDateTime());
                    }
                }
            }
        }
        return p;
    }

    /** Bulk create: will skip duplicates (unique constraint) and return number created */
    public int bulkCreateForSalle(Integer salleId, List<Place> places) throws SQLException {
        String sql = "INSERT INTO place (id_salle, rangee, numero, code_place, id_type_place) VALUES (?, ?, ?, ?, ?) ON CONFLICT (id_salle, rangee, numero) DO NOTHING";
        int created = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Place p : places) {
                ps.setInt(1, salleId);
                ps.setString(2, p.getRangee());
                ps.setObject(3, p.getNumero());
                ps.setString(4, p.getCodePlace());
                if (p.getIdTypePlace() != null) ps.setInt(5, p.getIdTypePlace()); else ps.setNull(5, Types.INTEGER);
                created += ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            logger.error("bulk create failed", e);
            throw e;
        }
        return created;
    }

    private Place map(ResultSet rs) throws SQLException {
        Place p = new Place();
        p.setId(rs.getInt("id"));
        p.setIdSalle(rs.getInt("id_salle"));
        p.setRangee(rs.getString("rangee"));
        p.setNumero((Integer) rs.getObject("numero"));
        p.setCodePlace(rs.getString("code_place"));
        p.setIdTypePlace((Integer) rs.getObject("id_type_place"));
        Object createdObj = null;
        try { createdObj = rs.getObject("cree_le"); } catch (SQLException ignore) {}
        if (createdObj != null) {
            if (createdObj instanceof LocalDateTime) p.setCreeLe((LocalDateTime) createdObj);
            else if (createdObj instanceof OffsetDateTime) p.setCreeLe(((OffsetDateTime) createdObj).toLocalDateTime());
            else {
                Timestamp ts = rs.getTimestamp("cree_le"); if (ts != null) p.setCreeLe(ts.toLocalDateTime());
            }
        }
        return p;
    }
}

package com.cinema.dao;

import com.cinema.model.Salle;
import com.cinema.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SalleDAO {
    private static final Logger logger = LoggerFactory.getLogger(SalleDAO.class);

    public Salle create(Salle s) throws SQLException {
        String sql = "INSERT INTO salle (nom, capacite) VALUES (?, ?) RETURNING id, nom, capacite, cree_le";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNom());
            ps.setObject(2, s.getCapacite());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s.setId(rs.getInt("id"));
                    s.setNom(rs.getString("nom"));
                    s.setCapacite((Integer) rs.getObject("capacite"));
                    Object createdObj = rs.getObject("cree_le");
                    if (createdObj != null) {
                        if (createdObj instanceof LocalDateTime) {
                            s.setCreeLe((LocalDateTime) createdObj);
                        } else if (createdObj instanceof OffsetDateTime) {
                            s.setCreeLe(((OffsetDateTime) createdObj).toLocalDateTime());
                        } else if (createdObj instanceof Timestamp) {
                            s.setCreeLe(((Timestamp) createdObj).toLocalDateTime());
                        } else {
                            Timestamp ts = rs.getTimestamp("cree_le");
                            if (ts != null) s.setCreeLe(ts.toLocalDateTime());
                        }
                    }
                }
            }
        }
        logger.info("Salle créée: {}", s);
        return s;
    }

    public Optional<Salle> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM salle WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    public List<Salle> findAll() throws SQLException {
        List<Salle> list = new ArrayList<>();
        String sql = "SELECT * FROM salle ORDER BY nom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean update(Salle s) throws SQLException {
        String sql = "UPDATE salle SET nom = ?, capacite = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNom());
            ps.setObject(2, s.getCapacite());
            ps.setInt(3, s.getId());
            int rows = ps.executeUpdate();
            if (rows > 0) logger.info("Salle mise à jour: {}", s);
            return rows > 0;
        }
    }

    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM salle WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) logger.info("Salle supprimée id={}", id);
            return rows > 0;
        }
    }

    public int countSeancesForSalle(Integer salleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM seance WHERE id_salle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, salleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public void deleteWithDependencies(Integer salleId) throws SQLException {
        String selectSeances = "SELECT id FROM seance WHERE id_salle = ?";
        String deleteTickets = "DELETE FROM ticket WHERE id_seance = ?";
        String deleteReservations = "DELETE FROM reservation WHERE id_seance = ?";
        String deleteSeance = "DELETE FROM seance WHERE id = ?";
        String deleteSalle = "DELETE FROM salle WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psSel = conn.prepareStatement(selectSeances)) {
                psSel.setInt(1, salleId);
                try (ResultSet rs = psSel.executeQuery()) {
                    while (rs.next()) {
                        int seanceId = rs.getInt("id");
                        try (PreparedStatement psDelTickets = conn.prepareStatement(deleteTickets)) {
                            psDelTickets.setInt(1, seanceId);
                            psDelTickets.executeUpdate();
                        }
                        try (PreparedStatement psDelRes = conn.prepareStatement(deleteReservations)) {
                            psDelRes.setInt(1, seanceId);
                            psDelRes.executeUpdate();
                        }
                        try (PreparedStatement psDelSeance = conn.prepareStatement(deleteSeance)) {
                            psDelSeance.setInt(1, seanceId);
                            psDelSeance.executeUpdate();
                        }
                    }
                }
            }

            try (PreparedStatement psDelSalle = conn.prepareStatement(deleteSalle)) {
                psDelSalle.setInt(1, salleId);
                psDelSalle.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { logger.warn("Rollback fail", ex); }
            throw e;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { logger.warn("Close fail", ex); }
        }
    }

    private Salle map(ResultSet rs) throws SQLException {
        Salle s = new Salle();
        s.setId(rs.getInt("id"));
        s.setNom(rs.getString("nom"));
        s.setCapacite((Integer) rs.getObject("capacite"));
        Object createdObj = rs.getObject("cree_le");
        if (createdObj != null) {
            if (createdObj instanceof LocalDateTime) s.setCreeLe((LocalDateTime) createdObj);
            else if (createdObj instanceof OffsetDateTime) s.setCreeLe(((OffsetDateTime) createdObj).toLocalDateTime());
            else if (createdObj instanceof Timestamp) s.setCreeLe(((Timestamp) createdObj).toLocalDateTime());
            else {
                Timestamp ts = rs.getTimestamp("cree_le");
                if (ts != null) s.setCreeLe(ts.toLocalDateTime());
            }
        }
        return s;
    }
}

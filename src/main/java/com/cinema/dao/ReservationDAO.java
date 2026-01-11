package com.cinema.dao;

import com.cinema.model.Reservation;
import com.cinema.util.DatabaseConnection;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.UUID;

public class ReservationDAO {

    public int create(Reservation r) throws SQLException {
        String sql = "INSERT INTO reservation (id_personne, id_seance, id_statut, montant_total, reference_unique) VALUES (?, ?, ?, ?, ?) RETURNING id, cree_le";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (r.getIdPersonne() != null) ps.setInt(1, r.getIdPersonne()); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, r.getIdSeance());
            if (r.getIdStatut() != null) ps.setInt(3, r.getIdStatut()); else ps.setNull(3, Types.INTEGER);
            ps.setBigDecimal(4, r.getMontantTotal() == null ? BigDecimal.ZERO : r.getMontantTotal());
            String ref = r.getReferenceUnique(); if (ref == null || ref.isBlank()) ref = UUID.randomUUID().toString();
            ps.setString(5, ref);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r.setId(rs.getInt("id"));
                    Object o = rs.getObject("cree_le");
                    if (o instanceof OffsetDateTime) r.setCreeLe(((OffsetDateTime)o).toLocalDateTime());
                    return r.getId();
                }
            }
        }
        return -1;
    }

    public Reservation findById(int id) throws SQLException {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Reservation> findAll() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation ORDER BY cree_le DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean updateMontantTotal(int reservationId, BigDecimal montant) throws SQLException {
        String sql = "UPDATE reservation SET montant_total = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, montant);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean changeStatut(int reservationId, int idStatut, Integer changePar, String commentaire) throws SQLException {
        String sql = "UPDATE reservation SET id_statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idStatut);
                ps.setInt(2, reservationId);
                ps.executeUpdate();
            }
            String hist = "INSERT INTO historique_statut_reservation (id_reservation, id_statut, change_par, commentaire) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps2 = conn.prepareStatement(hist)) {
                ps2.setInt(1, reservationId);
                ps2.setInt(2, idStatut);
                if (changePar != null) ps2.setInt(3, changePar); else ps2.setNull(3, Types.INTEGER);
                ps2.setString(4, commentaire);
                ps2.executeUpdate();
            }
            conn.commit();
            return true;
        }
    }

    public List<java.util.Map<String,Object>> getHistory(int reservationId) throws SQLException {
        List<java.util.Map<String,Object>> list = new ArrayList<>();
        String sql = "SELECT h.id, h.id_statut, s.libelle, h.date_changement, h.change_par, h.commentaire FROM historique_statut_reservation h JOIN statut_reservation s ON h.id_statut = s.id WHERE h.id_reservation = ? ORDER BY h.date_changement";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String,Object> m = new java.util.HashMap<>();
                    m.put("id", rs.getInt("id"));
                    m.put("id_statut", rs.getInt("id_statut"));
                    m.put("libelle", rs.getString("libelle"));
                    m.put("date_changement", rs.getObject("date_changement"));
                    m.put("change_par", rs.getObject("change_par"));
                    m.put("commentaire", rs.getString("commentaire"));
                    list.add(m);
                }
            }
        }
        return list;
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setIdPersonne((Integer) rs.getObject("id_personne"));
        r.setIdSeance((Integer) rs.getObject("id_seance"));
        r.setIdStatut((Integer) rs.getObject("id_statut"));
        r.setMontantTotal(rs.getBigDecimal("montant_total"));
        r.setReferenceUnique(rs.getString("reference_unique"));
        Object o = rs.getObject("cree_le"); if (o instanceof OffsetDateTime) r.setCreeLe(((OffsetDateTime)o).toLocalDateTime());
        return r;
    }
}

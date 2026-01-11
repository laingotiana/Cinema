package com.cinema.dao;

import com.cinema.model.Ticket;
import com.cinema.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class TicketDAO {

    public int create(Ticket t) throws SQLException {
        String sql = "INSERT INTO ticket (id_reservation, id_seance, id_place, id_statut, id_categorie_personne, prix, code_barre) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (t.getIdReservation() != null) ps.setInt(1, t.getIdReservation()); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, t.getIdSeance());
            ps.setInt(3, t.getIdPlace());
            if (t.getIdStatut() != null) ps.setInt(4, t.getIdStatut()); else ps.setNull(4, Types.INTEGER);
            if (t.getIdCategoriePersonne() != null) ps.setInt(5, t.getIdCategoriePersonne()); else ps.setNull(5, Types.INTEGER);
            ps.setBigDecimal(6, t.getPrix());
            ps.setString(7, t.getCodeBarre());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { t.setId(rs.getInt(1)); return t.getId(); }
            }
        }
        return -1;
    }

    public List<Ticket> findByReservation(int reservationId) throws SQLException {
        List<Ticket> list = new ArrayList<>();
        String sql = "SELECT * FROM ticket WHERE id_reservation = ? ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public BigDecimal sumPrixByReservation(int reservationId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(prix),0) FROM ticket WHERE id_reservation = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getBigDecimal(1); }
        }
        return BigDecimal.ZERO;
    }

    public boolean updateStatut(int ticketId, int idStatut, Integer changePar) throws SQLException {
        String sql = "UPDATE ticket SET id_statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idStatut);
                ps.setInt(2, ticketId);
                ps.executeUpdate();
            }
            String hist = "INSERT INTO historique_statut_ticket (id_ticket, id_statut, date_changement) VALUES (?, ?, now())";
            try (PreparedStatement ps2 = conn.prepareStatement(hist)) {
                ps2.setInt(1, ticketId);
                ps2.setInt(2, idStatut);
                ps2.executeUpdate();
            }
            conn.commit();
            return true;
        }
    }

    public Ticket findById(int id) throws SQLException {
        String sql = "SELECT * FROM ticket WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public java.util.List<java.util.Map<String,Object>> getHistory(int ticketId) throws SQLException {
        java.util.List<java.util.Map<String,Object>> list = new java.util.ArrayList<>();
        String sql = "SELECT h.id, h.id_statut, st.libelle, h.date_changement FROM historique_statut_ticket h JOIN statut_ticket st ON h.id_statut = st.id WHERE h.id_ticket = ? ORDER BY h.date_changement";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String,Object> m = new java.util.HashMap<>();
                    m.put("id", rs.getInt("id"));
                    m.put("id_statut", rs.getInt("id_statut"));
                    m.put("libelle", rs.getString("libelle"));
                    m.put("date_changement", rs.getObject("date_changement"));
                    list.add(m);
                }
            }
        }
        return list;
    }

    /* keep map as helper */
    private Ticket map(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getInt("id"));
        t.setIdReservation((Integer) rs.getObject("id_reservation"));
        t.setIdSeance((Integer) rs.getObject("id_seance"));
        t.setIdPlace((Integer) rs.getObject("id_place"));
        t.setIdStatut((Integer) rs.getObject("id_statut"));
        t.setIdCategoriePersonne((Integer) rs.getObject("id_categorie_personne"));
        t.setPrix(rs.getBigDecimal("prix"));
        t.setCodeBarre(rs.getString("code_barre"));
        return t;
    }
}

package com.cinema.dao;

import com.cinema.model.Seance;
import com.cinema.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    public List<Seance> findAll() throws SQLException {
        List<Seance> list = new ArrayList<>();
        String sql = "SELECT * FROM seance ORDER BY debut DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Seance findById(int id) throws SQLException {
        String sql = "SELECT * FROM seance WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Seance> findByFilm(int filmId) throws SQLException {
        List<Seance> list = new ArrayList<>();
        String sql = "SELECT * FROM seance WHERE id_film = ? ORDER BY debut";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Seance> findBySalle(int salleId) throws SQLException {
        List<Seance> list = new ArrayList<>();
        String sql = "SELECT * FROM seance WHERE id_salle = ? ORDER BY debut";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, salleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int create(Seance s) throws SQLException {
        String sql = "INSERT INTO seance (id_film, id_salle, debut, fin, langue) VALUES (?, ?, ?, ?, ?) RETURNING id, cree_le, fin";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getIdFilm());
            ps.setInt(2, s.getIdSalle());
            ps.setObject(3, s.getDebut());
            ps.setObject(4, s.getFin());
            ps.setString(5, s.getLangue());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    s.setId(id);
                    Object finObj = rs.getObject("fin");
                    if (finObj instanceof LocalDateTime) s.setFin((LocalDateTime) finObj);
                    else if (finObj instanceof OffsetDateTime) s.setFin(((OffsetDateTime) finObj).toLocalDateTime());
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean update(Seance s) throws SQLException {
        String sql = "UPDATE seance SET id_film = ?, id_salle = ?, debut = ?, fin = ?, langue = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getIdFilm());
            ps.setInt(2, s.getIdSalle());
            ps.setObject(3, s.getDebut());
            ps.setObject(4, s.getFin());
            ps.setString(5, s.getLangue());
            ps.setInt(6, s.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM seance WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Call DB function verifier_conflit_seance to detect conflicts */
    public boolean hasConflict(Integer salleId, Object debut, Object fin, Integer excludeSeanceId) throws SQLException {
        String sql = "SELECT verifier_conflit_seance(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, salleId);
            ps.setObject(2, debut);
            ps.setObject(3, fin);
            if (excludeSeanceId != null) ps.setInt(4, excludeSeanceId); else ps.setNull(4, Types.INTEGER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean(1);
            }
        }
        return false;
    }

    private Seance map(ResultSet rs) throws SQLException {
        Seance s = new Seance();
        s.setId(rs.getInt("id"));
        s.setIdFilm((Integer) rs.getObject("id_film"));
        s.setIdSalle((Integer) rs.getObject("id_salle"));
        Object debutObj = rs.getObject("debut");
        if (debutObj instanceof LocalDateTime) s.setDebut((LocalDateTime) debutObj);
        else if (debutObj instanceof OffsetDateTime) s.setDebut(((OffsetDateTime) debutObj).toLocalDateTime());
        else {
            Timestamp t = rs.getTimestamp("debut"); if (t != null) s.setDebut(t.toLocalDateTime());
        }
        Object finObj = null;
        try { finObj = rs.getObject("fin"); } catch (SQLException ignore) {}
        if (finObj != null) {
            if (finObj instanceof LocalDateTime) s.setFin((LocalDateTime) finObj);
            else if (finObj instanceof OffsetDateTime) s.setFin(((OffsetDateTime) finObj).toLocalDateTime());
            else { Timestamp t = rs.getTimestamp("fin"); if (t != null) s.setFin(t.toLocalDateTime()); }
        }
        s.setLangue(rs.getString("langue"));
        return s;
    }

}

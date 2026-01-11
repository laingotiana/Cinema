package com.cinema.dao;

import com.cinema.model.TarifSeance;
import com.cinema.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarifSeanceDAO {

    public List<TarifSeance> findAll() throws SQLException {
        List<TarifSeance> list = new ArrayList<>();
        String sql = "SELECT id, id_seance, id_type_place, id_categorie_personne, prix FROM tarif_seance ORDER BY id_seance, id_type_place, id_categorie_personne";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<TarifSeance> findBySeance(int idSeance) throws SQLException {
        List<TarifSeance> list = new ArrayList<>();
        String sql = "SELECT id, id_seance, id_type_place, id_categorie_personne, prix FROM tarif_seance WHERE id_seance = ? ORDER BY id_type_place, id_categorie_personne";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSeance);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int create(TarifSeance t) throws SQLException {
        String sql = "INSERT INTO tarif_seance (id_seance, id_type_place, id_categorie_personne, prix) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getIdSeance());
            ps.setObject(2, t.getIdTypePlace());
            ps.setObject(3, t.getIdCategoriePersonne());
            ps.setBigDecimal(4, t.getPrix());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { t.setId(rs.getInt(1)); return t.getId(); }
            }
        }
        return -1;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tarif_seance WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private TarifSeance map(ResultSet rs) throws SQLException {
        TarifSeance t = new TarifSeance();
        t.setId(rs.getInt("id"));
        t.setIdSeance((Integer) rs.getObject("id_seance"));
        t.setIdTypePlace((Integer) rs.getObject("id_type_place"));
        t.setIdCategoriePersonne((Integer) rs.getObject("id_categorie_personne"));
        t.setPrix(rs.getBigDecimal("prix"));
        return t;
    }
}

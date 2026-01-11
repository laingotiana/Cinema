package com.cinema.dao;

import com.cinema.model.TarifDefaut;
import com.cinema.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarifDefautDAO {

    public List<TarifDefaut> findAll() throws SQLException {
        List<TarifDefaut> list = new ArrayList<>();
        String sql = "SELECT id, id_type_place, id_categorie_personne, prix FROM tarif_defaut ORDER BY id_type_place, id_categorie_personne";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TarifDefaut t = new TarifDefaut();
                t.setId(rs.getInt("id"));
                t.setIdTypePlace((Integer) rs.getObject("id_type_place"));
                t.setIdCategoriePersonne((Integer) rs.getObject("id_categorie_personne"));
                t.setPrix(rs.getBigDecimal("prix"));
                list.add(t);
            }
        }
        return list;
    }

    public TarifDefaut findById(int id) throws SQLException {
        String sql = "SELECT id, id_type_place, id_categorie_personne, prix FROM tarif_defaut WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public int create(TarifDefaut t) throws SQLException {
        String sql = "INSERT INTO tarif_defaut (id_type_place, id_categorie_personne, prix) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, t.getIdTypePlace());
            ps.setObject(2, t.getIdCategoriePersonne());
            ps.setBigDecimal(3, t.getPrix());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    t.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean update(TarifDefaut t) throws SQLException {
        String sql = "UPDATE tarif_defaut SET id_type_place = ?, id_categorie_personne = ?, prix = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, t.getIdTypePlace());
            ps.setObject(2, t.getIdCategoriePersonne());
            ps.setBigDecimal(3, t.getPrix());
            ps.setInt(4, t.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tarif_defaut WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private TarifDefaut map(ResultSet rs) throws SQLException {
        TarifDefaut t = new TarifDefaut();
        t.setId(rs.getInt("id"));
        t.setIdTypePlace((Integer) rs.getObject("id_type_place"));
        t.setIdCategoriePersonne((Integer) rs.getObject("id_categorie_personne"));
        t.setPrix(rs.getBigDecimal("prix"));
        return t;
    }

}

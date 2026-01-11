package com.cinema.dao;

import com.cinema.model.CategoriePersonne;
import com.cinema.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriePersonneDAO {
    public List<CategoriePersonne> findAll() throws SQLException {
        List<CategoriePersonne> list = new ArrayList<>();
        String sql = "SELECT id, libelle FROM categorie_personne ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                CategoriePersonne c = new CategoriePersonne();
                c.setId(rs.getInt("id"));
                c.setLibelle(rs.getString("libelle"));
                list.add(c);
            }
        }
        return list;
    }
}

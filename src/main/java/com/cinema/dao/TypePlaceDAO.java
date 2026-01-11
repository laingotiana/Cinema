package com.cinema.dao;

import com.cinema.model.TypePlace;
import com.cinema.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypePlaceDAO {
    public List<TypePlace> findAll() throws SQLException {
        List<TypePlace> list = new ArrayList<>();
        String sql = "SELECT id, libelle FROM type_place ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                TypePlace t = new TypePlace();
                t.setId(rs.getInt("id"));
                t.setLibelle(rs.getString("libelle"));
                list.add(t);
            }
        }
        return list;
    }
}

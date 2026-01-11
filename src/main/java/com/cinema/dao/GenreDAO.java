package com.cinema.dao;

import com.cinema.model.Genre;
import com.cinema.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenreDAO {
    private static final Logger logger = LoggerFactory.getLogger(GenreDAO.class);

    public Genre create(Genre genre) throws SQLException {
        String sql = "INSERT INTO genre (libelle) VALUES (?) RETURNING id, libelle, /*cree_le*/ now() as cree_le";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genre.getLibelle());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    genre.setId(rs.getInt("id"));
                    genre.setLibelle(rs.getString("libelle"));
                    Object createdObj = rs.getObject("cree_le");
                    if (createdObj != null) {
                        if (createdObj instanceof LocalDateTime) {
                            genre.setCreeLe((LocalDateTime) createdObj);
                        } else if (createdObj instanceof OffsetDateTime) {
                            genre.setCreeLe(((OffsetDateTime) createdObj).toLocalDateTime());
                        } else if (createdObj instanceof Timestamp) {
                            genre.setCreeLe(((Timestamp) createdObj).toLocalDateTime());
                        } else {
                            Timestamp ts = rs.getTimestamp("cree_le");
                            if (ts != null) genre.setCreeLe(ts.toLocalDateTime());
                        }
                    }
                }
            }
        }
        logger.info("Genre créé: {}", genre);
        return genre;
    }

    public Optional<Genre> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM genre WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Genre g = map(rs);
                    return Optional.of(g);
                }
            }
        }
        return Optional.empty();
    }

    public List<Genre> findAll() throws SQLException {
        List<Genre> list = new ArrayList<>();
        String sql = "SELECT * FROM genre ORDER BY libelle";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public boolean update(Genre genre) throws SQLException {
        String sql = "UPDATE genre SET libelle = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genre.getLibelle());
            ps.setInt(2, genre.getId());
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM genre WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    private Genre map(ResultSet rs) throws SQLException {
        Genre g = new Genre();
        g.setId(rs.getInt("id"));
        g.setLibelle(rs.getString("libelle"));
        // Some schemas may not have a cree_le column for genre; handle absence gracefully
        Object createdObj = null;
        try {
            createdObj = rs.getObject("cree_le");
        } catch (SQLException ignore) {
            // column not present - ignore
        }
        if (createdObj != null) {
            if (createdObj instanceof LocalDateTime) {
                g.setCreeLe((LocalDateTime) createdObj);
            } else if (createdObj instanceof OffsetDateTime) {
                g.setCreeLe(((OffsetDateTime) createdObj).toLocalDateTime());
            } else if (createdObj instanceof Timestamp) {
                g.setCreeLe(((Timestamp) createdObj).toLocalDateTime());
            } else {
                Timestamp ts = null;
                try {
                    ts = rs.getTimestamp("cree_le");
                } catch (SQLException ignore) {
                    // ignore
                }
                if (ts != null) g.setCreeLe(ts.toLocalDateTime());
            }
        }
        return g;
    }
}

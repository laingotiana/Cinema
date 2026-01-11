package com.cinema.dao;

import com.cinema.model.Genre;
import com.cinema.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmGenreDAO {
    private static final Logger logger = LoggerFactory.getLogger(FilmGenreDAO.class);

    public List<Genre> findGenresForFilm(Integer filmId) throws SQLException {
        List<Genre> list = new ArrayList<>();
        String sql = "SELECT g.* FROM genre g JOIN film_genre fg ON g.id = fg.id_genre WHERE fg.id_film = ? ORDER BY g.libelle";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // reuse GenreDAO mapping logic by reading columns
                    com.cinema.model.Genre g = new com.cinema.model.Genre();
                    g.setId(rs.getInt("id"));
                    g.setLibelle(rs.getString("libelle"));
                    list.add(g);
                }
            }
        }
        return list;
    }

    public boolean addGenreToFilm(Integer filmId, Integer genreId) throws SQLException {
        String sql = "INSERT INTO film_genre (id_film, id_genre) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            ps.setInt(2, genreId);
            int rows = ps.executeUpdate();
            logger.info("Association film-genre ajoutée: film={}, genre={}", filmId, genreId);
            return rows > 0;
        }
    }

    public boolean removeGenreFromFilm(Integer filmId, Integer genreId) throws SQLException {
        String sql = "DELETE FROM film_genre WHERE id_film = ? AND id_genre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            ps.setInt(2, genreId);
            int rows = ps.executeUpdate();
            logger.info("Association film-genre supprimée: film={}, genre={}", filmId, genreId);
            return rows > 0;
        }
    }
}

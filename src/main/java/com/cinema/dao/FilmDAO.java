package com.cinema.dao;

import com.cinema.model.Film;
import com.cinema.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour l'entité Film
 * Gère les opérations CRUD sur la table film
 */
public class FilmDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(FilmDAO.class);
    
    /**
     * Créer un nouveau film
     */
    public Film create(Film film) throws SQLException {
        String sql = "INSERT INTO film (titre, description, duree_minutes, date_sortie, " +
                     "age_min, langue_originale, affiche_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, cree_le";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, film.getTitre());
            stmt.setString(2, film.getDescription());
            stmt.setObject(3, film.getDureeMinutes());
            stmt.setDate(4, film.getDateSortie());
            stmt.setObject(5, film.getAgeMin());
            stmt.setString(6, film.getLangueOriginale());
            stmt.setString(7, film.getAfficheUrl());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    film.setId(rs.getInt("id"));
                    // Handle possible TIMESTAMPTZ mapping robustly (driver may return OffsetDateTime, Timestamp, or LocalDateTime)
                    Object createdObj = rs.getObject("cree_le");
                    if (createdObj != null) {
                        if (createdObj instanceof LocalDateTime) {
                            film.setCreeLe((LocalDateTime) createdObj);
                        } else if (createdObj instanceof OffsetDateTime) {
                            film.setCreeLe(((OffsetDateTime) createdObj).toLocalDateTime());
                        } else if (createdObj instanceof Timestamp) {
                            film.setCreeLe(((Timestamp) createdObj).toLocalDateTime());
                        } else {
                            Timestamp ts = rs.getTimestamp("cree_le");
                            if (ts != null) {
                                film.setCreeLe(ts.toLocalDateTime());
                            }
                        }
                    }
                }
            }
            
            logger.info("Film créé avec succès: {}", film);
            return film;
        }
    }
    
    /**
     * Trouver un film par son ID
     */
    public Optional<Film> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM film WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFilm(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Trouver tous les films
     */
    public List<Film> findAll() throws SQLException {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM film ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                films.add(mapResultSetToFilm(rs));
            }
        }
        
        logger.debug("Nombre de films trouvés: {}", films.size());
        return films;
    }
    
    /**
     * Rechercher des films par titre (LIKE)
     */
    public List<Film> searchByTitre(String query) throws SQLException {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM film WHERE LOWER(titre) LIKE LOWER(?) ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + query + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    films.add(mapResultSetToFilm(rs));
                }
            }
        }
        
        return films;
    }
    
    /**
     * Trouver les films par genre
     */
    public List<Film> findByGenre(Integer genreId) throws SQLException {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT f.* FROM film f " +
                     "JOIN film_genre fg ON f.id = fg.id_film " +
                     "WHERE fg.id_genre = ? " +
                     "ORDER BY f.titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, genreId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    films.add(mapResultSetToFilm(rs));
                }
            }
        }
        
        return films;
    }
    
    /**
     * Mettre à jour un film
     */
    public boolean update(Film film) throws SQLException {
        String sql = "UPDATE film SET titre = ?, description = ?, duree_minutes = ?, " +
                     "date_sortie = ?, age_min = ?, langue_originale = ?, affiche_url = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, film.getTitre());
            stmt.setString(2, film.getDescription());
            stmt.setObject(3, film.getDureeMinutes());
            stmt.setDate(4, film.getDateSortie());
            stmt.setObject(5, film.getAgeMin());
            stmt.setString(6, film.getLangueOriginale());
            stmt.setString(7, film.getAfficheUrl());
            stmt.setInt(8, film.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Film mis à jour: {}", film);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Supprimer un film
     */
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM film WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Film supprimé: id={}", id);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Compte le nombre de séances référencant ce film
     */
    public int countSeancesForFilm(Integer filmId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM seance WHERE id_film = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filmId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Supprime les séances et leurs dépendances (tickets/reservations) puis le film dans une transaction.
     * Ceci effectue des suppressions en cascade manuelles pour éviter les violations de FK.
     */
    public void deleteWithDependencies(Integer filmId) throws SQLException {
        String selectSeances = "SELECT id FROM seance WHERE id_film = ?";
        String deleteTickets = "DELETE FROM ticket WHERE id_seance = ?";
        String deleteReservations = "DELETE FROM reservation WHERE id_seance = ?";
        String deleteSeance = "DELETE FROM seance WHERE id = ?";
        String deleteFilm = "DELETE FROM film WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psSel = conn.prepareStatement(selectSeances)) {
                psSel.setInt(1, filmId);
                try (ResultSet rs = psSel.executeQuery()) {
                    while (rs.next()) {
                        int seanceId = rs.getInt("id");

                        try (PreparedStatement psDelTickets = conn.prepareStatement(deleteTickets)) {
                            psDelTickets.setInt(1, seanceId);
                            psDelTickets.executeUpdate();
                        }

                        try (PreparedStatement psDelReservations = conn.prepareStatement(deleteReservations)) {
                            psDelReservations.setInt(1, seanceId);
                            psDelReservations.executeUpdate();
                        }

                        try (PreparedStatement psDelSeance = conn.prepareStatement(deleteSeance)) {
                            psDelSeance.setInt(1, seanceId);
                            psDelSeance.executeUpdate();
                        }
                    }
                }
            }

            try (PreparedStatement psDelFilm = conn.prepareStatement(deleteFilm)) {
                psDelFilm.setInt(1, filmId);
                psDelFilm.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.warn("Rollback failed", ex);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    logger.warn("Failed to close connection", ex);
                }
            }
        }
    }
    
    /**
     * Mapper un ResultSet vers un objet Film
     */
    private Film mapResultSetToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setTitre(rs.getString("titre"));
        film.setDescription(rs.getString("description"));
        film.setDureeMinutes((Integer) rs.getObject("duree_minutes"));
        film.setDateSortie(rs.getDate("date_sortie"));
        film.setAgeMin((Integer) rs.getObject("age_min"));
        film.setLangueOriginale(rs.getString("langue_originale"));
        film.setAfficheUrl(rs.getString("affiche_url"));
        // Handle possible TIMESTAMPTZ mapping
        Object createdObj = rs.getObject("cree_le");
        if (createdObj != null) {
            if (createdObj instanceof LocalDateTime) {
                film.setCreeLe((LocalDateTime) createdObj);
            } else if (createdObj instanceof OffsetDateTime) {
                film.setCreeLe(((OffsetDateTime) createdObj).toLocalDateTime());
            } else if (createdObj instanceof Timestamp) {
                film.setCreeLe(((Timestamp) createdObj).toLocalDateTime());
            } else {
                Timestamp ts = rs.getTimestamp("cree_le");
                if (ts != null) {
                    film.setCreeLe(ts.toLocalDateTime());
                }
            }
        }
        return film;
    }
}

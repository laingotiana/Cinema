package com.cinema.dao;

import com.cinema.model.Personne;
import com.cinema.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * DAO pour l'entité Personne (Utilisateur)
 * Gère les opérations CRUD et l'authentification
 */
public class PersonneDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(PersonneDAO.class);
    
    /**
     * Créer un nouvel utilisateur
     */
    public Personne create(Personne personne) throws SQLException {
        String sql = "INSERT INTO personne (nom_complet, email, telephone, mot_de_passe_hash, role) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id, cree_le";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, personne.getNomComplet());
            stmt.setString(2, personne.getEmail());
            stmt.setString(3, personne.getTelephone());
            stmt.setString(4, personne.getMotDePasseHash());
            stmt.setString(5, personne.getRole());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    personne.setId(rs.getInt("id"));
                    personne.setCreeLe(rs.getObject("cree_le", LocalDateTime.class));
                }
            }
            
            logger.info("Personne créée: {}", personne);
            return personne;
        }
    }
    
    /**
     * Trouver un utilisateur par son ID
     */
    public Optional<Personne> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM personne WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPersonne(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Trouver un utilisateur par email
     */
    public Optional<Personne> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM personne WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPersonne(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Vérifier si un email existe déjà
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM personne WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Mettre à jour un utilisateur
     */
    public boolean update(Personne personne) throws SQLException {
        String sql = "UPDATE personne SET nom_complet = ?, telephone = ?, role = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, personne.getNomComplet());
            stmt.setString(2, personne.getTelephone());
            stmt.setString(3, personne.getRole());
            stmt.setInt(4, personne.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Personne mise à jour: {}", personne);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Mettre à jour le mot de passe d'un utilisateur
     */
    public boolean updatePassword(Integer id, String newPasswordHash) throws SQLException {
        String sql = "UPDATE personne SET mot_de_passe_hash = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Mot de passe mis à jour pour personne id={}", id);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Mapper un ResultSet vers un objet Personne
     */
    private Personne mapResultSetToPersonne(ResultSet rs) throws SQLException {
        Personne personne = new Personne();
        personne.setId(rs.getInt("id"));
        personne.setNomComplet(rs.getString("nom_complet"));
        personne.setEmail(rs.getString("email"));
        personne.setTelephone(rs.getString("telephone"));
        personne.setMotDePasseHash(rs.getString("mot_de_passe_hash"));
        personne.setRole(rs.getString("role"));
        // Handle timestamptz -> java.time conversion safely
        Object createdObj = rs.getObject("cree_le");
        if (createdObj != null) {
            if (createdObj instanceof LocalDateTime) {
                personne.setCreeLe((LocalDateTime) createdObj);
            } else if (createdObj instanceof OffsetDateTime) {
                personne.setCreeLe(((OffsetDateTime) createdObj).toLocalDateTime());
            } else if (createdObj instanceof Timestamp) {
                personne.setCreeLe(((Timestamp) createdObj).toLocalDateTime());
            } else {
                // Fallback: try getTimestamp
                Timestamp ts = rs.getTimestamp("cree_le");
                if (ts != null) {
                    personne.setCreeLe(ts.toLocalDateTime());
                }
            }
        }
        return personne;
    }
}

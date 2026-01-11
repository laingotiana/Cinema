package com.cinema.model;

import java.time.LocalDateTime;

/**
 * Entité Personne (Utilisateur)
 */
public class Personne {
    private Integer id;
    private String nomComplet;
    private String email;
    private String telephone;
    private String motDePasseHash;
    private String role; // USER ou ADMIN
    private LocalDateTime creeLe;
    
    // Constructeurs
    public Personne() {}
    
    public Personne(Integer id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }
    
    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getMotDePasseHash() { return motDePasseHash; }
    public void setMotDePasseHash(String motDePasseHash) { this.motDePasseHash = motDePasseHash; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }
    
    // Méthodes utiles
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
    
    public boolean isUser() {
        return "USER".equalsIgnoreCase(role);
    }
    
    @Override
    public String toString() {
        return String.format("Personne[id=%d, email='%s', role='%s']", id, email, role);
    }
}

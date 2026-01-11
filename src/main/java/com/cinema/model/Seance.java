package com.cinema.model;

import java.time.LocalDateTime;

/**
 * Entité Séance
 */
public class Seance {
    private Integer id;
    private Integer idFilm;
    private Integer idSalle;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private String langue; // VF, VO, etc.
    private LocalDateTime creeLe;
    
    // Relations (optionnel, pour éviter les requêtes multiples)
    private Film film;
    private Salle salle;
    
    // Constructeurs
    public Seance() {}
    
    public Seance(Integer id, Integer idFilm, Integer idSalle, LocalDateTime debut) {
        this.id = id;
        this.idFilm = idFilm;
        this.idSalle = idSalle;
        this.debut = debut;
    }
    
    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getIdFilm() { return idFilm; }
    public void setIdFilm(Integer idFilm) { this.idFilm = idFilm; }
    
    public Integer getIdSalle() { return idSalle; }
    public void setIdSalle(Integer idSalle) { this.idSalle = idSalle; }
    
    public LocalDateTime getDebut() { return debut; }
    public void setDebut(LocalDateTime debut) { this.debut = debut; }
    
    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }
    
    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }
    
    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }
    
    public Film getFilm() { return film; }
    public void setFilm(Film film) { this.film = film; }
    
    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }

    // Helper accessors for JSP formatting (returns java.util.Date for JSTL fmt)
    public java.util.Date getDebutDate() {
        if (this.debut == null) return null;
        return java.util.Date.from(this.debut.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    public java.util.Date getFinDate() {
        if (this.fin == null) return null;
        return java.util.Date.from(this.fin.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
    
    @Override
    public String toString() {
        return String.format("Seance[id=%d, film=%d, salle=%d, debut=%s]", 
            id, idFilm, idSalle, debut);
    }
}

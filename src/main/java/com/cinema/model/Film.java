package com.cinema.model;

import java.time.LocalDateTime;

/**
 * Entité Film
 */
public class Film {
    private Integer id;
    private String titre;
    private String description;
    private Integer dureeMinutes;
    private java.sql.Date dateSortie;
    private Integer ageMin;
    private String langueOriginale;
    private String afficheUrl;
    private LocalDateTime creeLe;
    
    // Constructeurs
    public Film() {}
    
    public Film(Integer id, String titre) {
        this.id = id;
        this.titre = titre;
    }
    
    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    
    public java.sql.Date getDateSortie() { return dateSortie; }
    public void setDateSortie(java.sql.Date dateSortie) { this.dateSortie = dateSortie; }
    
    public Integer getAgeMin() { return ageMin; }
    public void setAgeMin(Integer ageMin) { this.ageMin = ageMin; }
    
    public String getLangueOriginale() { return langueOriginale; }
    public void setLangueOriginale(String langueOriginale) { this.langueOriginale = langueOriginale; }
    
    public String getAfficheUrl() { return afficheUrl; }
    public void setAfficheUrl(String afficheUrl) { this.afficheUrl = afficheUrl; }
    
    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }
    
    @Override
    public String toString() {
        return String.format("Film[id=%d, titre='%s', duree=%dmin]", id, titre, dureeMinutes);
    }
}

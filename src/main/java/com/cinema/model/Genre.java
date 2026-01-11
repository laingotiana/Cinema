package com.cinema.model;

import java.time.LocalDateTime;

/**
 * Entité Genre
 */
public class Genre {
    private Integer id;
    private String libelle;
    private LocalDateTime creeLe;

    public Genre() {}

    public Genre(Integer id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }

    @Override
    public String toString() {
        return String.format("Genre[id=%d, libelle='%s']", id, libelle);
    }
}

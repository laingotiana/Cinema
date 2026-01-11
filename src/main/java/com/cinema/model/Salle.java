package com.cinema.model;

import java.time.LocalDateTime;

/**
 * Entité Salle
 */
public class Salle {
    private Integer id;
    private String nom;
    private Integer capacite;
    private LocalDateTime creeLe;

    // Constructeurs
    public Salle() {}

    public Salle(Integer id, String nom, Integer capacite) {
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }

    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }

    @Override
    public String toString() {
        return String.format("Salle[id=%d, nom='%s', capacite=%d]", id, nom, capacite);
    }

}


package com.cinema.model;

public class TypePlace {
    private Integer id;
    private String libelle;

    public TypePlace() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    @Override
    public String toString() { return String.format("TypePlace[id=%d, libelle=%s]", id, libelle); }
}

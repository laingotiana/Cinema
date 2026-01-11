package com.cinema.model;

import java.time.LocalDateTime;

public class Place {
    private Integer id;
    private Integer idSalle;
    private String rangee;
    private Integer numero;
    private String codePlace;
    private Integer idTypePlace;
    private LocalDateTime creeLe;

    public Place() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdSalle() { return idSalle; }
    public void setIdSalle(Integer idSalle) { this.idSalle = idSalle; }

    public String getRangee() { return rangee; }
    public void setRangee(String rangee) { this.rangee = rangee; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getCodePlace() { return codePlace; }
    public void setCodePlace(String codePlace) { this.codePlace = codePlace; }

    public Integer getIdTypePlace() { return idTypePlace; }
    public void setIdTypePlace(Integer idTypePlace) { this.idTypePlace = idTypePlace; }

    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }

    @Override
    public String toString() {
        return String.format("Place[id=%d, salle=%d, %s%d, type=%d]", id, idSalle, rangee, numero, idTypePlace);
    }
}

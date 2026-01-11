package com.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ticket {
    private Integer id;
    private Integer idReservation;
    private Integer idSeance;
    private Integer idPlace;
    private Integer idStatut;
    private Integer idCategoriePersonne;
    private BigDecimal prix;
    private String codeBarre;
    private LocalDateTime creeLe;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdReservation() { return idReservation; }
    public void setIdReservation(Integer idReservation) { this.idReservation = idReservation; }
    public Integer getIdSeance() { return idSeance; }
    public void setIdSeance(Integer idSeance) { this.idSeance = idSeance; }
    public Integer getIdPlace() { return idPlace; }
    public void setIdPlace(Integer idPlace) { this.idPlace = idPlace; }
    public Integer getIdStatut() { return idStatut; }
    public void setIdStatut(Integer idStatut) { this.idStatut = idStatut; }
    public Integer getIdCategoriePersonne() { return idCategoriePersonne; }
    public void setIdCategoriePersonne(Integer idCategoriePersonne) { this.idCategoriePersonne = idCategoriePersonne; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }
    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }
}

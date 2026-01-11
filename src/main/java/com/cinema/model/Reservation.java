package com.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Reservation {
    private Integer id;
    private Integer idPersonne;
    private Integer idSeance;
    private Integer idStatut;
    private BigDecimal montantTotal;
    private String referenceUnique;
    private LocalDateTime creeLe;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdPersonne() { return idPersonne; }
    public void setIdPersonne(Integer idPersonne) { this.idPersonne = idPersonne; }
    public Integer getIdSeance() { return idSeance; }
    public void setIdSeance(Integer idSeance) { this.idSeance = idSeance; }
    public Integer getIdStatut() { return idStatut; }
    public void setIdStatut(Integer idStatut) { this.idStatut = idStatut; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    public String getReferenceUnique() { return referenceUnique; }
    public void setReferenceUnique(String referenceUnique) { this.referenceUnique = referenceUnique; }
    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }
}

package com.cinema.model;

import java.math.BigDecimal;

public class TarifDefaut {
    private Integer id;
    private Integer idTypePlace;
    private Integer idCategoriePersonne;
    private BigDecimal prix;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdTypePlace() { return idTypePlace; }
    public void setIdTypePlace(Integer idTypePlace) { this.idTypePlace = idTypePlace; }

    public Integer getIdCategoriePersonne() { return idCategoriePersonne; }
    public void setIdCategoriePersonne(Integer idCategoriePersonne) { this.idCategoriePersonne = idCategoriePersonne; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
}

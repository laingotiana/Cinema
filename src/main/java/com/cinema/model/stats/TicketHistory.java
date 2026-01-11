package com.cinema.model.stats;

import java.time.OffsetDateTime;

public class TicketHistory {
    private Integer ticketId;
    private String filmTitre;
    private OffsetDateTime createdAt;
    private double prix;
    private String statut;
    private String codeBarre;

    public Integer getTicketId() { return ticketId; }
    public void setTicketId(Integer ticketId) { this.ticketId = ticketId; }
    public String getFilmTitre() { return filmTitre; }
    public void setFilmTitre(String filmTitre) { this.filmTitre = filmTitre; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }
}

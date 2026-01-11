package com.cinema.model.stats;

import java.time.OffsetDateTime;

public class SalleOccupancy {
    private Integer seanceId;
    private Integer salleId;
    private String salleNom;
    private OffsetDateTime debut;
    private int capacite;
    private int ticketsSold;

    public Integer getSeanceId() { return seanceId; }
    public void setSeanceId(Integer seanceId) { this.seanceId = seanceId; }
    public Integer getSalleId() { return salleId; }
    public void setSalleId(Integer salleId) { this.salleId = salleId; }
    public String getSalleNom() { return salleNom; }
    public void setSalleNom(String salleNom) { this.salleNom = salleNom; }
    public OffsetDateTime getDebut() { return debut; }
    public void setDebut(OffsetDateTime debut) { this.debut = debut; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public int getTicketsSold() { return ticketsSold; }
    public void setTicketsSold(int ticketsSold) { this.ticketsSold = ticketsSold; }
    public double getOccupancyRate() { return capacite > 0 ? (ticketsSold * 100.0) / capacite : 0.0; }
}

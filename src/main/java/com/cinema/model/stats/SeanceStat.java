package com.cinema.model.stats;

import java.time.OffsetDateTime;

public class SeanceStat {
    private Integer seanceId;
    private String filmTitre;
    private OffsetDateTime debut;
    private int ticketsSold;
    private double revenue;

    public Integer getSeanceId() { return seanceId; }
    public void setSeanceId(Integer seanceId) { this.seanceId = seanceId; }
    public String getFilmTitre() { return filmTitre; }
    public void setFilmTitre(String filmTitre) { this.filmTitre = filmTitre; }
    public OffsetDateTime getDebut() { return debut; }
    public void setDebut(OffsetDateTime debut) { this.debut = debut; }
    public int getTicketsSold() { return ticketsSold; }
    public void setTicketsSold(int ticketsSold) { this.ticketsSold = ticketsSold; }
    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }
}

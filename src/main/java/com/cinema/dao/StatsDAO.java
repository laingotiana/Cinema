package com.cinema.dao;

import com.cinema.model.stats.FilmRevenue;
import com.cinema.model.stats.SeanceStat;
import com.cinema.model.stats.SalleOccupancy;
import com.cinema.model.stats.TicketHistory;
import com.cinema.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatsDAO {
    private static final Logger logger = LoggerFactory.getLogger(StatsDAO.class);

    public List<SeanceStat> ticketsAndRevenueBySeance() throws SQLException {
        String sql = "SELECT s.id as seance_id, f.titre as film_titre, s.debut, " +
                "COUNT(t.id) - COUNT(CASE WHEN st.code='ANNULE' THEN 1 END) as tickets_sold, " +
                "COALESCE(SUM(CASE WHEN st.code='ANNULE' THEN 0 ELSE t.prix END),0) as revenue " +
                "FROM seance s " +
                "LEFT JOIN film f ON s.id_film = f.id " +
                "LEFT JOIN ticket t ON t.id_seance = s.id " +
                "LEFT JOIN statut_ticket st ON t.id_statut = st.id " +
                "GROUP BY s.id, f.titre, s.debut " +
                "ORDER BY s.debut DESC";

        List<SeanceStat> out = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SeanceStat s = new SeanceStat();
                s.setSeanceId(rs.getInt("seance_id"));
                s.setFilmTitre(rs.getString("film_titre"));
                Object dt = rs.getObject("debut");
                if (dt instanceof OffsetDateTime) s.setDebut((OffsetDateTime) dt);
                else if (dt != null) s.setDebut(((java.sql.Timestamp)dt).toInstant().atOffset(java.time.ZoneOffset.UTC));
                s.setTicketsSold(rs.getInt("tickets_sold"));
                s.setRevenue(rs.getDouble("revenue"));
                out.add(s);
            }
        }
        return out;
    }

    public List<FilmRevenue> revenueByFilm() throws SQLException {
        String sql = "SELECT f.id as film_id, f.titre as film_titre, " +
                "COALESCE(SUM(CASE WHEN st.code='ANNULE' THEN 0 ELSE t.prix END),0) as revenue " +
                "FROM film f " +
                "LEFT JOIN seance s ON s.id_film = f.id " +
                "LEFT JOIN ticket t ON t.id_seance = s.id " +
                "LEFT JOIN statut_ticket st ON t.id_statut = st.id " +
                "GROUP BY f.id, f.titre " +
                "ORDER BY revenue DESC";

        List<FilmRevenue> out = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FilmRevenue fr = new FilmRevenue();
                fr.setFilmId(rs.getInt("film_id"));
                fr.setFilmTitre(rs.getString("film_titre"));
                fr.setRevenue(rs.getDouble("revenue"));
                out.add(fr);
            }
        }
        return out;
    }

    public List<SalleOccupancy> occupancyBySalle() throws SQLException {
        String sql = "SELECT s.id as seance_id, sa.id as salle_id, sa.nom as salle_nom, sa.capacite, s.debut, " +
                "COUNT(t.id) - COUNT(CASE WHEN st.code='ANNULE' THEN 1 END) as tickets_sold " +
                "FROM seance s " +
                "JOIN salle sa ON s.id_salle = sa.id " +
                "LEFT JOIN ticket t ON t.id_seance = s.id " +
                "LEFT JOIN statut_ticket st ON t.id_statut = st.id " +
                "GROUP BY s.id, sa.id, sa.nom, sa.capacite, s.debut " +
                "ORDER BY sa.nom, s.debut DESC";

        List<SalleOccupancy> out = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SalleOccupancy so = new SalleOccupancy();
                so.setSeanceId(rs.getInt("seance_id"));
                so.setSalleId(rs.getInt("salle_id"));
                so.setSalleNom(rs.getString("salle_nom"));
                so.setCapacite(rs.getInt("capacite"));
                Object dt = rs.getObject("debut");
                if (dt instanceof OffsetDateTime) so.setDebut((OffsetDateTime) dt);
                else if (dt != null) so.setDebut(((java.sql.Timestamp)dt).toInstant().atOffset(java.time.ZoneOffset.UTC));
                so.setTicketsSold(rs.getInt("tickets_sold"));
                out.add(so);
            }
        }
        return out;
    }

    public List<TicketHistory> recentSalesHistory(int limit) throws SQLException {
        String sql = "SELECT t.id as ticket_id, f.titre as film_titre, t.cree_le as created_at, t.prix, st.code as statut, t.code_barre " +
                "FROM ticket t " +
                "LEFT JOIN seance s ON t.id_seance = s.id " +
                "LEFT JOIN film f ON s.id_film = f.id " +
                "LEFT JOIN statut_ticket st ON t.id_statut = st.id " +
                "ORDER BY t.cree_le DESC LIMIT ?";

        List<TicketHistory> out = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TicketHistory th = new TicketHistory();
                    th.setTicketId(rs.getInt("ticket_id"));
                    th.setFilmTitre(rs.getString("film_titre"));
                    Object dt = rs.getObject("created_at");
                    if (dt instanceof OffsetDateTime) th.setCreatedAt((OffsetDateTime)dt);
                    else if (dt != null) th.setCreatedAt(((java.sql.Timestamp)dt).toInstant().atOffset(java.time.ZoneOffset.UTC));
                    th.setPrix(rs.getDouble("prix"));
                    th.setStatut(rs.getString("statut"));
                    th.setCodeBarre(rs.getString("code_barre"));
                    out.add(th);
                }
            }
        }
        return out;
    }
}

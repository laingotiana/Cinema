<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.cinema.model.stats.SeanceStat, com.cinema.model.stats.FilmRevenue, com.cinema.model.stats.SalleOccupancy, com.cinema.model.stats.TicketHistory" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Statistiques</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <style> .small-muted { font-size: 0.9em; color:#666; } </style>
</head>
<body>
<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h1>Statistiques</h1>
    <a href="<%=request.getContextPath()%>/admin" class="btn btn-secondary">Retour</a>
  </div>

  <h3>Tickets vendus par séance</h3>
  <table class="table table-sm table-striped">
    <thead>
      <tr><th>Séance</th><th>Film</th><th>Début</th><th>Tickets vendus</th><th>Recette</th></tr>
    </thead>
    <tbody>
    <%
      List<SeanceStat> seanceStats = (List<SeanceStat>) request.getAttribute("seanceStats");
      if (seanceStats != null) {
        for (SeanceStat s : seanceStats) {
    %>
      <tr>
        <td><a href="<%=request.getContextPath()%>/admin/seance?id=<%=s.getSeanceId()%>%>"><%=s.getSeanceId()%></a></td>
        <td><%= s.getFilmTitre() != null ? s.getFilmTitre() : "-" %></td>
        <td><%= s.getDebut() != null ? s.getDebut().toString() : "-" %></td>
        <td><%= s.getTicketsSold() %></td>
        <td><%= String.format("%.2f", s.getRevenue()) %> €</td>
      </tr>
    <%
        }
      }
    %>
    </tbody>
  </table>

  <h3>Recette par film</h3>
  <table class="table table-sm table-striped">
    <thead><tr><th>Film</th><th>Recette</th></tr></thead>
    <tbody>
    <%
      List<FilmRevenue> filmRevenue = (List<FilmRevenue>) request.getAttribute("filmRevenue");
      if (filmRevenue != null) {
        for (FilmRevenue fr : filmRevenue) {
    %>
      <tr>
        <td><%= fr.getFilmTitre() != null ? fr.getFilmTitre() : "-" %></td>
        <td><%= String.format("%.2f", fr.getRevenue()) %> €</td>
      </tr>
    <%
        }
      }
    %>
    </tbody>
  </table>

  <h3>Taux de remplissage par salle (par séance)</h3>
  <table class="table table-sm table-striped">
    <thead><tr><th>Salle</th><th>Séance</th><th>Début</th><th>Capacité</th><th>Vendues</th><th>Taux</th></tr></thead>
    <tbody>
    <%
      List<SalleOccupancy> occ = (List<SalleOccupancy>) request.getAttribute("occupancy");
      if (occ != null) {
        for (SalleOccupancy so : occ) {
    %>
      <tr>
        <td><%= so.getSalleNom() %></td>
        <td><a href="<%=request.getContextPath()%>/admin/seance?id=<%=so.getSeanceId()%>%>">#<%=so.getSeanceId()%></a></td>
        <td><%= so.getDebut()!=null?so.getDebut().toString():"-" %></td>
        <td><%= so.getCapacite() %></td>
        <td><%= so.getTicketsSold() %></td>
        <td><%= String.format("%.1f", so.getOccupancyRate()) %> %</td>
      </tr>
    <%
        }
      }
    %>
    </tbody>
  </table>

  <h3>Historique des ventes (récent)</h3>
  <table class="table table-sm table-striped">
    <thead><tr><th>Ticket</th><th>Film</th><th>Date</th><th>Prix</th><th>Statut</th><th>Code</th></tr></thead>
    <tbody>
    <%
      List<TicketHistory> history = (List<TicketHistory>) request.getAttribute("history");
      if (history != null) {
        for (TicketHistory th : history) {
    %>
      <tr>
        <td><a href="<%=request.getContextPath()%>/admin/ticket/detail?id=<%=th.getTicketId()%>%>">#<%=th.getTicketId()%></a></td>
        <td><%= th.getFilmTitre()!=null?th.getFilmTitre():"-" %></td>
        <td><%= th.getCreatedAt()!=null?th.getCreatedAt().toString():"-" %></td>
        <td><%= String.format("%.2f", th.getPrix()) %> €</td>
        <td><%= th.getStatut()!=null?th.getStatut():"-" %></td>
        <td><%= th.getCodeBarre()!=null?th.getCodeBarre():"-" %></td>
      </tr>
    <%
        }
      }
    %>
    </tbody>
  </table>

</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

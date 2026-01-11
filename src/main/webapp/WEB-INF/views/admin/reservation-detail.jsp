<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.cinema.model.Ticket, com.cinema.model.Reservation" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Détails réservation</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h1>Détails réservation</h1>
  <% Reservation r = (Reservation) request.getAttribute("reservation"); %>
  <% if (r == null) { %>
    <div class="alert alert-warning">Réservation introuvable</div>
  <% } else { %>
    <p><strong>Référence:</strong> <%= r.getReferenceUnique() %></p>
    <p><strong>Séance:</strong> <%= r.getIdSeance() %></p>
    <p><strong>Montant total:</strong> <%= r.getMontantTotal() %></p>

    <h3>Tickets</h3>
    <table class="table table-sm table-bordered">
      <thead><tr><th>#</th><th>Place</th><th>Catégorie</th><th>Prix</th><th>Statut</th></tr></thead>
      <tbody>
      <%
        List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
        if (tickets != null) {
          for (Ticket t : tickets) {
      %>
        <tr>
          <td><%= t.getId() %></td>
          <td><%= t.getIdPlace() %></td>
          <td><%= t.getIdCategoriePersonne() %></td>
          <td><%= t.getPrix() %></td>
          <td><%= t.getIdStatut() %></td>
        </tr>
      <%    }
        }
      %>
      </tbody>
    </table>

    <h4>Ajouter un ticket</h4>
    <form method="post" action="<%=request.getContextPath()%>/admin/reservation/detail">
      <input type="hidden" name="action" value="addTicket" />
      <input type="hidden" name="id_reservation" value="<%=r.getId()%>" />
      <div class="row">
        <div class="col-md-4 mb-3">
          <label>Id place</label>
          <input type="text" name="id_place" class="form-control" />
        </div>
        <div class="col-md-4 mb-3">
          <label>Id catégorie personne</label>
          <input type="text" name="id_categorie_personne" class="form-control" />
        </div>
        <div class="col-md-4 mb-3 align-self-end">
          <button class="btn btn-primary">Ajouter</button>
        </div>
      </div>
    </form>

    <h3>Historique des statuts</h3>
    <table class="table table-sm table-bordered">
      <thead><tr><th>Date</th><th>Statut</th><th>Commentaire</th></tr></thead>
      <tbody>
      <% List<java.util.Map<String,Object>> hist = (List<java.util.Map<String,Object>>) request.getAttribute("history"); if (hist != null) {
          for (java.util.Map<String,Object> h : hist) {
      %>
        <tr>
          <td><%= h.get("date_changement") %></td>
          <td><%= h.get("libelle") %></td>
          <td><%= h.get("commentaire") %></td>
        </tr>
      <%    }
        }
      %>
      </tbody>
    </table>
  <% } %>
  <a href="<%=request.getContextPath()%>/admin/reservations" class="btn btn-link">Retour</a>
</div>
</body>
</html>

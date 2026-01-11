<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.cinema.model.Ticket, java.util.List" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Détail ticket</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h1>Détail du ticket</h1>
  <% Ticket t = (Ticket) request.getAttribute("ticket"); %>
  <% if (t == null) { %>
    <div class="alert alert-warning">Ticket introuvable</div>
  <% } else { %>
    <table class="table table-sm">
      <tr><th>ID</th><td><%= t.getId() %></td></tr>
      <tr><th>Séance</th><td><%= t.getIdSeance() %></td></tr>
      <tr><th>Place</th><td><%= t.getIdPlace() %></td></tr>
      <tr><th>Catégorie</th><td><%= t.getIdCategoriePersonne() %></td></tr>
      <tr><th>Prix</th><td><%= t.getPrix() %></td></tr>
      <tr><th>Statut (id)</th><td><%= t.getIdStatut() %></td></tr>
      <tr><th>Code-barre</th><td><%= t.getCodeBarre() %></td></tr>
      <tr><th>Créé le</th><td><%= t.getCreeLe() %></td></tr>
    </table>

    <h3>Changer le statut</h3>
    <form method="post" action="<%=request.getContextPath()%>/admin/ticket/status">
      <input type="hidden" name="ticket_id" value="<%= t.getId() %>" />
      <div class="row mb-3">
        <div class="col-md-4">
          <label class="form-label">Nouveau statut (id)</label>
          <input type="text" name="id_statut" class="form-control" />
        </div>
        <div class="col-md-4 align-self-end">
          <button class="btn btn-primary">Mettre à jour</button>
        </div>
      </div>
    </form>

    <h3>Historique des statuts</h3>
    <table class="table table-sm table-bordered">
      <thead><tr><th>Date</th><th>Statut</th></tr></thead>
      <tbody>
      <% List<java.util.Map<String,Object>> hist = (List<java.util.Map<String,Object>>) request.getAttribute("history");
         if (hist != null) {
           for (java.util.Map<String,Object> h : hist) {
      %>
        <tr>
          <td><%= h.get("date_changement") %></td>
          <td><%= h.get("libelle") %></td>
        </tr>
      <%   }
         }
      %>
      </tbody>
    </table>
  <% } %>

  <a href="<%=request.getContextPath()%>/admin/reservations" class="btn btn-link">Retour</a>
</div>
</body>
</html>

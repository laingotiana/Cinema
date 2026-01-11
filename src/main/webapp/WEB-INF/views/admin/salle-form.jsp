<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.cinema.model.Salle" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Formulaire salle</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h1><%= request.getAttribute("salle") != null ? "Modifier la salle" : "Ajouter une salle" %></h1>
  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <form method="post" action="<%=request.getContextPath()%>/admin/salles">
    <% Salle s = (Salle) request.getAttribute("salle"); %>
    <input type="hidden" name="id" value="<%= s != null && s.getId() != null ? s.getId() : "" %>" />

    <div class="mb-3">
      <label class="form-label">Nom</label>
      <input class="form-control" name="nom" value="<%= s != null && s.getNom() != null ? s.getNom() : "" %>" required />
    </div>
    <div class="mb-3">
      <label class="form-label">Capacité</label>
      <input class="form-control" name="capacite" value="<%= s != null && s.getCapacite() != null ? s.getCapacite() : "" %>" />
      <div class="form-text">Entrez un entier positif</div>
    </div>

    <div class="d-flex gap-2">
      <button class="btn btn-primary" type="submit">Enregistrer</button>
      <a class="btn btn-secondary" href="<%=request.getContextPath()%>/admin/salles">Annuler</a>
    </div>
  </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

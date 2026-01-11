<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Créer réservation</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h1>Créer une réservation</h1>
  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <form method="post" action="<%=request.getContextPath()%>/admin/reservation">
    <div class="mb-3">
      <label class="form-label">ID personne (optionnel)</label>
      <input type="text" name="id_personne" class="form-control" />
    </div>
    <div class="mb-3">
      <label class="form-label">ID séance</label>
      <input type="text" name="id_seance" class="form-control" />
    </div>
    <div class="mb-3">
      <label class="form-label">Statut (id)</label>
      <input type="text" name="id_statut" class="form-control" />
    </div>
    <button class="btn btn-primary">Créer</button>
    <a class="btn btn-link" href="<%=request.getContextPath()%>/admin/reservations">Annuler</a>
  </form>
</div>
</body>
</html>

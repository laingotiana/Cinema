<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Créer ticket (vente sur place)</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h1>Vente sur place - Créer un ticket</h1>
  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <form method="post" action="<%=request.getContextPath()%>/admin/ticket">
    <div class="mb-3">
      <label class="form-label">ID séance</label>
      <input type="text" name="id_seance" class="form-control" />
    </div>
    <div class="mb-3">
      <label class="form-label">ID place</label>
      <input type="text" name="id_place" class="form-control" />
    </div>
    <div class="mb-3">
      <label class="form-label">ID catégorie personne</label>
      <input type="text" name="id_categorie_personne" class="form-control" />
    </div>
    <button class="btn btn-primary">Créer</button>
    <a class="btn btn-link" href="<%=request.getContextPath()%>/admin/reservations">Annuler</a>
  </form>
</div>
</body>
</html>

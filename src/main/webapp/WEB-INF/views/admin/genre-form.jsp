<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.cinema.model.Genre" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Formulaire genre</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h1><%= request.getAttribute("genre") != null ? "Modifier le genre" : "Ajouter un genre" %></h1>
  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <form method="post" action="<%=request.getContextPath()%>/admin/genres">
    <% Genre g = (Genre) request.getAttribute("genre"); %>
    <input type="hidden" name="id" value="<%= g != null && g.getId() != null ? g.getId() : "" %>" />

    <div class="mb-3">
      <label class="form-label">Libellé</label>
      <input class="form-control" name="libelle" value="<%= g != null && g.getLibelle() != null ? g.getLibelle() : "" %>" required />
    </div>

    <div class="d-flex gap-2">
      <button class="btn btn-primary" type="submit">Enregistrer</button>
      <a class="btn btn-secondary" href="<%=request.getContextPath()%>/admin/genres">Annuler</a>
    </div>
  </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

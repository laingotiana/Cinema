<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.cinema.model.Film" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Formulaire film</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h1><%= request.getAttribute("film") != null ? "Modifier le film" : "Ajouter un film" %></h1>
  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <form method="post" action="<%=request.getContextPath()%>/admin/films">
    <% Film film = (Film) request.getAttribute("film"); %>
    <input type="hidden" name="id" value="<%= film != null && film.getId() != null ? film.getId() : "" %>" />

    <div class="mb-3">
      <label class="form-label">Titre</label>
      <input class="form-control" name="titre" value="<%= film != null ? film.getTitre() : "" %>" required />
    </div>
    <div class="mb-3">
      <label class="form-label">Description</label>
      <textarea class="form-control" name="description" rows="4"><%= film != null && film.getDescription() != null ? film.getDescription() : "" %></textarea>
    </div>
    <div class="row">
      <div class="col-md-3 mb-3">
        <label class="form-label">Durée (min)</label>
        <input class="form-control" name="duree_minutes" value="<%= film != null && film.getDureeMinutes() != null ? film.getDureeMinutes() : "" %>" />
      </div>
      <div class="col-md-3 mb-3">
        <label class="form-label">Date sortie</label>
        <input class="form-control" type="date" name="date_sortie" value="<%= film != null && film.getDateSortie() != null ? film.getDateSortie().toString() : "" %>" />
      </div>
      <div class="col-md-3 mb-3">
        <label class="form-label">Âge min</label>
        <input class="form-control" name="age_min" value="<%= film != null && film.getAgeMin() != null ? film.getAgeMin() : "" %>" />
      </div>
      <div class="col-md-3 mb-3">
        <label class="form-label">Langue originale</label>
        <input class="form-control" name="langue_originale" value="<%= film != null && film.getLangueOriginale() != null ? film.getLangueOriginale() : "" %>" />
      </div>
    </div>

    <div class="mb-3">
      <label class="form-label">URL affiche</label>
      <input class="form-control" name="affiche_url" value="<%= film != null && film.getAfficheUrl() != null ? film.getAfficheUrl() : "" %>" />
    </div>

    <div class="d-flex gap-2">
      <button class="btn btn-primary" type="submit">Enregistrer</button>
      <a class="btn btn-secondary" href="<%=request.getContextPath()%>/admin/films">Annuler</a>
    </div>
  </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

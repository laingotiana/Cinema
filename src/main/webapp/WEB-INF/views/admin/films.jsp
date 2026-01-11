<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.cinema.model.Film" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Gestion des films</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h1>Films</h1>
    <div>
      <a href="<%=request.getContextPath()%>/admin/film" class="btn btn-success">Ajouter un film</a>
    </div>
  </div>

  <div class="mb-3">
    <form method="get" action="<%=request.getContextPath()%>/admin/films" class="row g-2 align-items-center">
      <div class="col-auto">
        <label class="visually-hidden" for="genreId">Genre</label>
        <select id="genreId" name="genreId" class="form-select">
          <option value="">Tous les genres</option>
          <%
            java.util.List<com.cinema.model.Genre> genres = (java.util.List<com.cinema.model.Genre>) request.getAttribute("genres");
            Integer selectedGenreId = (Integer) request.getAttribute("selectedGenreId");
            if (genres != null) {
              for (com.cinema.model.Genre g : genres) {
          %>
          <option value="<%=g.getId()%>" <%= (selectedGenreId!=null && selectedGenreId.equals(g.getId()))?"selected":"" %>><%=g.getLibelle()%></option>
          <%    }
            }
          %>
        </select>
      </div>
      <div class="col-auto">
        <button class="btn btn-outline-primary" type="submit">Filtrer</button>
        <a href="<%=request.getContextPath()%>/admin/films" class="btn btn-link">Effacer</a>
      </div>
    </form>
  </div>

  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <table class="table table-striped">
    <thead>
      <tr>
        <th>#</th>
        <th>Titre</th>
        <th>Description</th>
        <th>Durée</th>
        <th>Date sortie</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody>
    <%
      List<Film> films = (List<Film>) request.getAttribute("films");
      if (films != null) {
        for (Film film : films) {
    %>
      <tr>
        <td><%= film.getId() %></td>
        <td><%= film.getTitre() %></td>
        <td><%= film.getDescription() != null && film.getDescription().length() > 80 ? film.getDescription().substring(0,80)+"..." : film.getDescription() %></td>
        <td><%= film.getDureeMinutes() != null ? film.getDureeMinutes()+" min" : "-" %></td>
        <td><%= film.getDateSortie() != null ? film.getDateSortie().toString() : "-" %></td>
        <td>
          <a class="btn btn-sm btn-info" href="<%=request.getContextPath()%>/admin/film/detail?id=<%=film.getId()%>">Détails</a>
          <a class="btn btn-sm btn-primary" href="<%=request.getContextPath()%>/admin/film?id=<%=film.getId()%>">Editer</a>
          <form method="get" action="<%=request.getContextPath()%>/admin/films" style="display:inline" onsubmit="return confirm('Supprimer ce film ?');">
            <input type="hidden" name="action" value="delete" />
            <input type="hidden" name="id" value="<%=film.getId()%>" />
            <button class="btn btn-sm btn-danger" type="submit">Supprimer</button>
          </form>
            <form method="get" action="<%=request.getContextPath()%>/admin/films" style="display:inline" onsubmit="return confirm('Supprimer ce film et toutes ses séances/réservations associées ? Cette opération est irréversible.');">
              <input type="hidden" name="action" value="delete" />
              <input type="hidden" name="id" value="<%=film.getId()%>" />
              <input type="hidden" name="force" value="true" />
              <button class="btn btn-sm btn-outline-danger" type="submit">Supprimer (forcer)</button>
            </form>
        </td>
      </tr>
    <%
        }
      }
    %>
    </tbody>
  </table>

  <a href="<%=request.getContextPath()%>/admin" class="btn btn-link">Retour au tableau de bord</a>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

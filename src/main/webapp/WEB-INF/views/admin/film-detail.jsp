<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.cinema.model.Genre, com.cinema.model.Film" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Détails du film</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h1>Détails du film</h1>
    <a href="<%=request.getContextPath()%>/admin/films" class="btn btn-secondary">Retour</a>
  </div>

  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <%
    Film film = (Film) request.getAttribute("film");
    List<Genre> associated = (List<Genre>) request.getAttribute("associatedGenres");
    List<Genre> all = (List<Genre>) request.getAttribute("allGenres");
  %>

  <div class="card mb-3">
    <div class="row g-0">
      <div class="col-md-4">
        <% if (film.getAfficheUrl() != null && !film.getAfficheUrl().isBlank()) { %>
          <img src="<%= film.getAfficheUrl() %>" class="img-fluid rounded-start" alt="Affiche">
        <% } else { %>
          <div class="p-4">Aucune affiche</div>
        <% } %>
      </div>
      <div class="col-md-8">
        <div class="card-body">
          <h5 class="card-title"><%= film.getTitre() %></h5>
          <p class="card-text"><%= film.getDescription() != null ? film.getDescription() : "-" %></p>
          <p class="card-text"><small class="text-muted">Durée: <%= film.getDureeMinutes() != null ? film.getDureeMinutes()+" min" : "-" %>
            | Date sortie: <%= film.getDateSortie() != null ? film.getDateSortie().toString() : "-" %>
            | Age minimum: <%= film.getAgeMin() != null ? film.getAgeMin() : "-" %>
          </small></p>
        </div>
      </div>
    </div>
  </div>

  <div class="mb-4">
    <h4>Genres associés</h4>
    <div>
      <% if (associated != null && !associated.isEmpty()) { %>
        <ul class="list-inline">
          <% for (Genre g : associated) { %>
            <li class="list-inline-item">
              <span class="badge bg-primary"><%= g.getLibelle() %></span>
              <form method="post" action="<%=request.getContextPath()%>/admin/film/genres" style="display:inline">
                <input type="hidden" name="action" value="remove" />
                <input type="hidden" name="filmId" value="<%= film.getId() %>" />
                <input type="hidden" name="genreId" value="<%= g.getId() %>" />
                <button class="btn btn-sm btn-outline-light" type="submit" onclick="return confirm('Retirer ce genre ?');">✖</button>
              </form>
            </li>
          <% } %>
        </ul>
      <% } else { %>
        <p>Aucun genre associé.</p>
      <% } %>
    </div>
  </div>

  <div class="mb-4">
    <h4>Ajouter un genre</h4>
    <form method="post" action="<%=request.getContextPath()%>/admin/film/genres">
      <input type="hidden" name="action" value="add" />
      <input type="hidden" name="filmId" value="<%= film.getId() %>" />
      <div class="input-group">
        <select name="genreId" class="form-select">
          <option value="">-- Choisir --</option>
          <%
            if (all != null) {
              for (Genre g : all) {
                // show all, admin can add duplicates - DB ON CONFLICT prevents duplicates
          %>
            <option value="<%= g.getId() %>"><%= g.getLibelle() %></option>
          <%    }
            }
          %>
        </select>
        <button class="btn btn-primary" type="submit">Ajouter</button>
      </div>
    </form>
  </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

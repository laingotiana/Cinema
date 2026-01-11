<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.cinema.model.Genre" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Genres</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h1>Genres</h1>
    <a href="<%=request.getContextPath()%>/admin/genre" class="btn btn-success">Ajouter un genre</a>
  </div>

  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <table class="table table-striped">
    <thead>
      <tr>
        <th>#</th>
        <th>Libellé</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody>
    <%
      List<Genre> genres = (List<Genre>) request.getAttribute("genres");
      if (genres != null) {
        for (Genre g : genres) {
    %>
      <tr>
        <td><%= g.getId() %></td>
        <td><%= g.getLibelle() %></td>
        <td>
          <a class="btn btn-sm btn-primary" href="<%=request.getContextPath()%>/admin/genre?id=<%=g.getId()%>">Editer</a>
          <form method="get" action="<%=request.getContextPath()%>/admin/genres" style="display:inline" onsubmit="return confirm('Supprimer ce genre ?');">
            <input type="hidden" name="action" value="delete" />
            <input type="hidden" name="id" value="<%=g.getId()%>" />
            <button class="btn btn-sm btn-danger" type="submit">Supprimer</button>
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

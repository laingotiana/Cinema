<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.cinema.model.Salle" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Salles</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h1>Salles</h1>
    <a href="<%=request.getContextPath()%>/admin/salle" class="btn btn-success">Ajouter une salle</a>
  </div>

  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <table class="table table-striped">
    <thead>
      <tr>
        <th>#</th>
        <th>Nom</th>
        <th>Capacité</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody>
    <%
      List<Salle> salles = (List<Salle>) request.getAttribute("salles");
      if (salles != null) {
        for (Salle s : salles) {
    %>
      <tr>
        <td><%= s.getId() %></td>
        <td><%= s.getNom() %></td>
        <td><%= s.getCapacite() != null ? s.getCapacite() : "-" %></td>
        <td>
          <a class="btn btn-sm btn-primary" href="<%=request.getContextPath()%>/admin/salle?id=<%=s.getId()%>">Editer</a>
          <a class="btn btn-sm btn-secondary" href="<%=request.getContextPath()%>/admin/places?salleId=<%=s.getId()%>">Gérer les places</a>
          <form method="post" action="<%=request.getContextPath()%>/admin/salle/delete" style="display:inline" onsubmit="return confirm('Supprimer cette salle ?');">
            <input type="hidden" name="id" value="<%=s.getId()%>" />
            <button class="btn btn-sm btn-danger" type="submit">Supprimer</button>
          </form>
          <form method="post" action="<%=request.getContextPath()%>/admin/salle/delete" style="display:inline" onsubmit="return confirm('Supprimer cette salle et toutes ses séances associées ?');">
            <input type="hidden" name="id" value="<%=s.getId()%>" />
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

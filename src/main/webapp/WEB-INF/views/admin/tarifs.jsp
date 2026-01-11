<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.cinema.model.TarifDefaut, com.cinema.model.TarifSeance, com.cinema.model.TypePlace, com.cinema.model.CategoriePersonne" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Tarifs</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h1>Tarifs</h1>
    <div>
      <a href="<%=request.getContextPath()%>/admin/tarif" class="btn btn-success">Ajouter tarif par défaut</a>
      <a href="<%=request.getContextPath()%>/admin/tarif-seance" class="btn btn-secondary">Ajouter tarif pour séance</a>
    </div>
  </div>

  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <h3>Tarifs par défaut</h3>
  <table class="table table-sm table-bordered">
    <thead><tr><th>#</th><th>Type</th><th>Catégorie</th><th>Prix</th><th>Actions</th></tr></thead>
    <tbody>
    <%
      List<TarifDefaut> defauts = (List<TarifDefaut>) request.getAttribute("tarifsDefaut");
      List<TypePlace> types = (List<TypePlace>) request.getAttribute("types");
      List<CategoriePersonne> categories = (List<CategoriePersonne>) request.getAttribute("categories");
      if (defauts != null) {
        for (TarifDefaut t : defauts) {
    %>
      <tr>
        <td><%= t.getId() %></td>
        <td><%= t.getIdTypePlace() %></td>
        <td><%= t.getIdCategoriePersonne() %></td>
        <td><%= t.getPrix() %></td>
        <td>
          <a class="btn btn-sm btn-primary" href="<%=request.getContextPath()%>/admin/tarif?id=<%=t.getId()%>">Editer</a>
          <form method="post" action="<%=request.getContextPath()%>/admin/tarif/delete" style="display:inline" onsubmit="return confirm('Supprimer ce tarif ?');">
            <input type="hidden" name="id" value="<%=t.getId()%>" />
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

  <h3>Tarifs spécifiques par séance</h3>
  <table class="table table-sm table-bordered">
    <thead><tr><th>#</th><th>Séance</th><th>Type</th><th>Catégorie</th><th>Prix</th><th>Actions</th></tr></thead>
    <tbody>
    <%
      List<TarifSeance> tseances = (List<TarifSeance>) request.getAttribute("tarifsSeance");
      if (tseances != null) {
        for (TarifSeance t : tseances) {
    %>
      <tr>
        <td><%= t.getId() %></td>
        <td><%= t.getIdSeance() %></td>
        <td><%= t.getIdTypePlace() %></td>
        <td><%= t.getIdCategoriePersonne() %></td>
        <td><%= t.getPrix() %></td>
        <td>
          <form method="post" action="<%=request.getContextPath()%>/admin/tarif-seance/delete" style="display:inline" onsubmit="return confirm('Supprimer ce tarif séance ?');">
            <input type="hidden" name="id" value="<%=t.getId()%>" />
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

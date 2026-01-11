<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.cinema.model.TarifSeance, com.cinema.model.TypePlace, com.cinema.model.CategoriePersonne, com.cinema.model.Seance" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Tarif séance</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h1>Ajouter tarif pour séance</h1>

  <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
  <% } %>

  <form method="post" action="<%=request.getContextPath()%>/admin/tarif-seance">
    <div class="mb-3">
      <label class="form-label">Séance</label>
      <select name="id_seance" class="form-select">
        <option value="">-- choisir --</option>
        <% List<Seance> seances = (List<Seance>) request.getAttribute("seances"); if (seances != null) {
            for (Seance s : seances) { %>
          <option value="<%=s.getId()%>">#<%=s.getId()%> - <%=s.getDebut()%></option>
        <% } } %>
      </select>
    </div>

    <div class="mb-3">
      <label class="form-label">Type de place</label>
      <select name="id_type_place" class="form-select">
        <option value="">-- choisir --</option>
        <% List<TypePlace> types = (List<TypePlace>) request.getAttribute("types"); if (types != null) { for (TypePlace tp : types) { %>
          <option value="<%=tp.getId()%>"><%=tp.getLibelle()%></option>
        <% } } %>
      </select>
    </div>

    <div class="mb-3">
      <label class="form-label">Catégorie personne</label>
      <select name="id_categorie_personne" class="form-select">
        <option value="">-- choisir --</option>
        <% List<CategoriePersonne> cats = (List<CategoriePersonne>) request.getAttribute("categories"); if (cats != null) { for (CategoriePersonne c : cats) { %>
          <option value="<%=c.getId()%>"><%=c.getLibelle()%></option>
        <% } } %>
      </select>
    </div>

    <div class="mb-3">
      <label class="form-label">Prix</label>
      <input type="text" name="prix" class="form-control" />
    </div>

    <button class="btn btn-primary">Enregistrer</button>
    <a href="<%=request.getContextPath()%>/admin/tarifs" class="btn btn-link">Annuler</a>
  </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

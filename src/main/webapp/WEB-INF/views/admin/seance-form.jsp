<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <title>Créer / Editer une séance</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h1>${seance != null ? 'Editer la séance' : 'Nouvelle séance'}</h1>

  <form method="post" action="${pageContext.request.contextPath}/admin/seances">
    <c:if test="${seance != null}">
      <input type="hidden" name="id" value="${seance.id}" />
    </c:if>

    <div class="mb-3">
      <label class="form-label">Film</label>
      <select name="id_film" class="form-select" required>
        <option value="">-- choisir --</option>
        <c:forEach var="f" items="${films}">
          <option value="${f.id}" ${seance != null && seance.idFilm == f.id ? 'selected' : ''}>${f.titre}</option>
        </c:forEach>
      </select>
    </div>

    <div class="mb-3">
      <label class="form-label">Salle</label>
      <select name="id_salle" class="form-select" required>
        <option value="">-- choisir --</option>
        <c:forEach var="sa" items="${salles}">
          <option value="${sa.id}" ${seance != null && seance.idSalle == sa.id ? 'selected' : ''}>${sa.nom}</option>
        </c:forEach>
      </select>
    </div>

    <div class="mb-3">
      <label class="form-label">Début</label>
      <input type="datetime-local" name="debut" class="form-control" required value="${debutLocal}" />
    </div>

    <div class="mb-3">
      <label class="form-label">Langue</label>
      <input type="text" name="langue" class="form-control" value="${seance.langue}" />
    </div>

    <button class="btn btn-primary" type="submit">Enregistrer</button>
    <a class="btn btn-link" href="${pageContext.request.contextPath}/admin/seances">Annuler</a>
  </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
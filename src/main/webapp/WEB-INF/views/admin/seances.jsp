<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <title>Gestion des séances</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h1>Séances</h1>
    <a href="${pageContext.request.contextPath}/admin/seance" class="btn btn-success">Ajouter une séance</a>
  </div>

  <c:if test="${not empty flash_error}">
    <div class="alert alert-danger">${flash_error}</div>
  </c:if>
  <c:if test="${not empty flash_success}">
    <div class="alert alert-success">${flash_success}</div>
  </c:if>

  <table class="table table-striped">
    <thead>
      <tr>
        <th>#</th>
        <th>Film</th>
        <th>Salle</th>
        <th>Début</th>
        <th>Fin</th>
        <th>Langue</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="s" items="${seances}">
        <tr>
          <td>${s.id}</td>
          <td>${s.idFilm}</td>
          <td>${s.idSalle}</td>
          <td><fmt:formatDate value="${s.debutDate}" pattern="yyyy-MM-dd HH:mm"/></td>
          <td><fmt:formatDate value="${s.finDate}" pattern="yyyy-MM-dd HH:mm"/></td>
          <td>${s.langue}</td>
          <td>
            <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/admin/seance?id=${s.id}">Editer</a>
            <form method="post" action="${pageContext.request.contextPath}/admin/seance/delete" style="display:inline" onsubmit="return confirm('Supprimer cette séance ?');">
              <input type="hidden" name="id" value="${s.id}" />
              <button class="btn btn-sm btn-danger" type="submit">Supprimer</button>
            </form>
            <a class="btn btn-sm btn-secondary" href="${pageContext.request.contextPath}/seance/places?id_seance=${s.id}">Voir places (JSON)</a>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <a href="${pageContext.request.contextPath}/admin" class="btn btn-link">Retour au tableau de bord</a>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
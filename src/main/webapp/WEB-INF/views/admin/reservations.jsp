<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.cinema.model.Reservation" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Réservations</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h1>Réservations</h1>
    <a href="<%=request.getContextPath()%>/admin/reservation" class="btn btn-success">Créer réservation</a>
  </div>

  <table class="table table-sm table-bordered">
    <thead><tr><th>#</th><th>Référence</th><th>Séance</th><th>Montant</th><th>Actions</th></tr></thead>
    <tbody>
    <%
      List<Reservation> res = (List<Reservation>) request.getAttribute("reservations");
      if (res != null) { for (Reservation r : res) {
    %>
      <tr>
        <td><%= r.getId() %></td>
        <td><%= r.getReferenceUnique() %></td>
        <td><%= r.getIdSeance() %></td>
        <td><%= r.getMontantTotal() %></td>
        <td>
          <a class="btn btn-sm btn-primary" href="<%=request.getContextPath()%>/admin/reservation/detail?id=<%=r.getId()%>">Détails</a>
        </td>
      </tr>
    <% } } %>
    </tbody>
  </table>
</div>
</body>
</html>

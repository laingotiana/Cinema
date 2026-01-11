<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Admin - Tableau de bord</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body { padding-top: 56px; }
  </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
  <div class="container-fluid">
    <a class="navbar-brand" href="#">Cinema Admin</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarsExampleDefault" aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarsExampleDefault">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="crudDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">Gestion</a>
          <ul class="dropdown-menu" aria-labelledby="crudDropdown">
            <li><a class="dropdown-item" href="<%=request.getContextPath()%>/admin/films">Films</a></li>
            <li><a class="dropdown-item" href="<%=request.getContextPath()%>/admin/genres">Genres</a></li>
            <li><a class="dropdown-item" href="<%=request.getContextPath()%>/admin/salles">Salles</a></li>
            <li><a class="dropdown-item" href="<%=request.getContextPath()%>/admin/places">Places</a></li>
            <li><a class="dropdown-item" href="<%=request.getContextPath()%>/admin/seances">Séances</a></li>
            <li><a class="dropdown-item" href="<%=request.getContextPath()%>/admin/tarifs">Tarifs</a></li>
            <li><a class="dropdown-item" href="<%=request.getContextPath()%>/admin/reservations">Réservations</a></li>
            <li><a class="dropdown-item" href="<%=request.getContextPath()%>/admin/ticket">Tickets</a></li>
          </ul>
        </li>
        <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/admin/stats">Statistiques</a></li>
      </ul>
      <ul class="navbar-nav ms-auto">
        <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/logout">Se déconnecter</a></li>
      </ul>
    </div>
  </div>
</nav>

<main class="container mt-4">
  <div class="row">
    <div class="col-12">
      <h1>Tableau de bord administrateur</h1>
      <p class="lead">Bienvenue dans l'espace d'administration — ici vous pouvez gérer les films, salles et séances.</p>

      <div class="row">
        <div class="col-md-4">
          <div class="card mb-3">
            <div class="card-body">
              <h5 class="card-title">Films</h5>
              <p class="card-text">Gérer le catalogue de films.</p>
              <a href="<%=request.getContextPath()%>/admin/films" class="btn btn-primary">Gérer</a>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card mb-3">
            <div class="card-body">
              <h5 class="card-title">Salles</h5>
              <p class="card-text">Configurer les salles et places.</p>
              <a href="<%=request.getContextPath()%>/admin/salles" class="btn btn-primary">Gérer</a>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card mb-3">
            <div class="card-body">
              <h5 class="card-title">Statistiques</h5>
              <p class="card-text">Visualiser les ventes, recettes et taux de remplissage.</p>
              <a href="<%=request.getContextPath()%>/admin/stats" class="btn btn-success">Voir</a>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

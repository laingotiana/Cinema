<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Générer les places</title>
</head>
<body>
<h1>Générer les places pour la salle ${salleId}</h1>
<c:if test="${not empty flash_error}">
    <div style="color:red">${flash_error}</div>
</c:if>
<c:if test="${not empty flash_success}">
    <div style="color:green">${flash_success}</div>
</c:if>
<form method="post" action="${pageContext.request.contextPath}/admin/places/generate">
    <input type="hidden" name="salleId" value="${salleId}" />
    <label>Nombre de rangées: <input type="number" name="rows" value="5" min="1" required></label><br />
    <label>Nombre de colonnes par rangée: <input type="number" name="cols" value="10" min="1" required></label><br />
    <label>Numéro de départ: <input type="number" name="startNumber" value="1" min="1"></label><br />
    <label>Type par défaut:
        <select name="typeId">
            <option value="">(aucun)</option>
            <c:forEach var="t" items="${types}">
                <option value="${t.id}">${t.libelle}</option>
            </c:forEach>
        </select>
    </label>
    <br />
    <button type="submit">Générer</button>
</form>
<p><a href="${pageContext.request.contextPath}/admin/places?salleId=${salleId}">Voir les places</a></p>
</body>
</html>
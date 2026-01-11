<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Places - Salle ${salleId}</title>
</head>
<body>
<h1>Places pour la salle ${salleId}</h1>
<c:if test="${not empty flash_error}">
    <div style="color:red">${flash_error}</div>
</c:if>
<c:if test="${not empty flash_success}">
    <div style="color:green">${flash_success}</div>
</c:if>
<p><a href="${pageContext.request.contextPath}/admin/places/generate?salleId=${salleId}">Générer / ajouter des places</a></p>
<table border="1">
    <thead>
    <tr>
        <th>Rangée</th>
        <th>Numéro</th>
        <th>Code</th>
        <th>Type</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="p" items="${places}">
        <tr>
            <td>${p.rangee}</td>
            <td>${p.numero}</td>
            <td>${p.codePlace}</td>
            <td>
                <c:choose>
                    <c:when test="${not empty p.idTypePlace}">
                        <c:forEach var="t" items="${types}">
                            <c:if test="${t.id == p.idTypePlace}">${t.libelle}</c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<p><a href="${pageContext.request.contextPath}/admin/salles">Retour aux salles</a></p>
</body>
</html>
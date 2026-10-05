<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.railway.model.Station" %>
<%@ page import="com.railway.model.Route" %>
<%@ page import="java.util.List" %>
<%
    if (!"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Station> stations =
            (List<Station>) request.getAttribute("stations");

    List<Route> routes =
            (List<Route>) request.getAttribute("routes");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Stations & Routes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>

<header class="admin-header">
    <div class="admin-header-inner">
        <div>
            <h1>Digital Railway</h1>
            <p>Admin controls the stations and routes shown to users.</p>
        </div>
        <a class="header-link" href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</header>

<main class="admin-container">

    <div class="admin-nav">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">
            Dashboard
        </a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error-message"><%= request.getAttribute("error") %></div>
    <% } %>

    <section class="admin-card">
        <h2>Add Station</h2>
        <form class="admin-form" method="post"
              action="${pageContext.request.contextPath}/admin/network">
            <input type="hidden" name="action" value="addStation">

            <label>Station Code</label>
            <input type="text" name="stationCode" placeholder="Example: TVM" required>

            <label>Station Name</label>
            <input type="text" name="stationName" placeholder="Example: Thiruvananthapuram" required>

            <button class="button primary-button" type="submit">Add Station</button>
        </form>
    </section>

    <section class="admin-card">
        <h2>Existing Stations</h2>
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Change</th>
                    </tr>
                </thead>
                <tbody>
                <% if (stations != null) {
                    for (Station station : stations) { %>
                    <tr>
                        <td><%= station.getStationCode() %></td>
                        <td><%= station.getStationName() %></td>
                        <td>
                            <form class="inline-form" method="post"
                                  action="${pageContext.request.contextPath}/admin/network">
                                <input type="hidden" name="action" value="updateStation">
                                <input type="hidden" name="stationId" value="<%= station.getStationId() %>">
                                <input type="text" name="stationCode" value="<%= station.getStationCode() %>" required>
                                <input type="text" name="stationName" value="<%= station.getStationName() %>" required>
                                <button class="button small-button primary-button" type="submit">Update</button>
                            </form>
                        </td>
                    </tr>
                <%  }
                   } %>
                </tbody>
            </table>
        </div>
    </section>

    <section class="admin-card">
        <h2>Add Route</h2>
        <form class="admin-form" method="post"
              action="${pageContext.request.contextPath}/admin/network">
            <input type="hidden" name="action" value="addRoute">

            <label>Route Name</label>
            <input type="text" name="routeName" placeholder="Example: TVM - Ernakulam" required>

            <label>From Station</label>
            <select name="sourceStationId" required>
                <option value="">Select station</option>
                <% if (stations != null) {
                    for (Station station : stations) { %>
                    <option value="<%= station.getStationId() %>">
                        <%= station.getStationCode() %> - <%= station.getStationName() %>
                    </option>
                <%  }
                   } %>
            </select>

            <label>To Station</label>
            <select name="destinationStationId" required>
                <option value="">Select station</option>
                <% if (stations != null) {
                    for (Station station : stations) { %>
                    <option value="<%= station.getStationId() %>">
                        <%= station.getStationCode() %> - <%= station.getStationName() %>
                    </option>
                <%  }
                   } %>
            </select>

            <button class="button primary-button" type="submit">Add Route</button>
        </form>
    </section>

    <section class="admin-card">
        <h2>Existing Routes</h2>
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Route</th>
                        <th>From</th>
                        <th>To</th>
                        <th>Change</th>
                    </tr>
                </thead>
                <tbody>
                <% if (routes != null) {
                    for (Route route : routes) { %>
                    <tr>
                        <td><%= route.getRouteName() %></td>
                        <td><%= route.getSourceStation() %></td>
                        <td><%= route.getDestinationStation() %></td>
                        <td>
                            <form class="stacked-inline-form" method="post"
                                  action="${pageContext.request.contextPath}/admin/network">
                                <input type="hidden" name="action" value="updateRoute">
                                <input type="hidden" name="routeId" value="<%= route.getRouteId() %>">
                                <input type="text" name="routeName" value="<%= route.getRouteName() %>" required>
                                <select name="sourceStationId" required>
                                    <% if (stations != null) {
                                        for (Station station : stations) { %>
                                        <option value="<%= station.getStationId() %>"
                                            <%= station.getStationId() == route.getSourceStationId() ? "selected" : "" %>>
                                            <%= station.getStationCode() %> - <%= station.getStationName() %>
                                        </option>
                                    <%  }
                                       } %>
                                </select>
                                <select name="destinationStationId" required>
                                    <% if (stations != null) {
                                        for (Station station : stations) { %>
                                        <option value="<%= station.getStationId() %>"
                                            <%= station.getStationId() == route.getDestinationStationId() ? "selected" : "" %>>
                                            <%= station.getStationCode() %> - <%= station.getStationName() %>
                                        </option>
                                    <%  }
                                       } %>
                                </select>
                                <button class="button small-button primary-button" type="submit">Update</button>
                            </form>
                        </td>
                    </tr>
                <%  }
                   } %>
                </tbody>
            </table>
        </div>
    </section>

</main>
</body>
</html>

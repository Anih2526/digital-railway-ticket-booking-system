
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.railway.model.Schedule" %>
<%@ page import="com.railway.model.Train" %>
<%@ page import="com.railway.model.Route" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>

<%
    if (session.getAttribute("userId") == null
            || !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Schedule> schedules =
            (List<Schedule>) request.getAttribute("schedules");

    List<Train> trains =
            (List<Train>) request.getAttribute("trains");

    List<Route> routes =
            (List<Route>) request.getAttribute("routes");

    DateTimeFormatter displayFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    DateTimeFormatter inputFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Schedules & Fare</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin.css">
</head>

<body>

<header class="admin-header">
    <div class="admin-header-inner">
        <div>
            <h1>Digital Railway</h1>
            <p>Schedule & Fare Management</p>
        </div>

        <a class="header-link"
           href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</header>

<main class="admin-container">

    <div class="admin-nav">
        <a href="${pageContext.request.contextPath}/admin/dashboard">
            Dashboard
        </a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error-message">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <!-- ADD NEW SCHEDULE -->

    <section class="admin-card">
        <h2>Add Schedule</h2>

        <p>
            Create the train date, timings, route and fare
            that users will see.
        </p>

        <form class="admin-form" method="post"
              action="${pageContext.request.contextPath}/admin/schedules">

            <input type="hidden" name="action" value="add">

            <label>Train</label>
            <select name="trainId" required>
                <option value="">Select train</option>

                <% if (trains != null) {
                    for (Train train : trains) { %>

                        <option value="<%= train.getTrainId() %>">
                            <%= train.getTrainNumber() %> -
                            <%= train.getTrainName() %>
                        </option>

                <%  }
                   } %>
            </select>

            <label>Route</label>
            <select name="routeId" required>
                <option value="">Select route</option>

                <% if (routes != null) {
                    for (Route route : routes) { %>

                        <option value="<%= route.getRouteId() %>">
                            <%= route.getSourceStation() %> →
                            <%= route.getDestinationStation() %>
                        </option>

                <%  }
                   } %>
            </select>

            <label>Departure Date & Time</label>
            <input type="datetime-local"
                   name="departureTime" required>

            <label>Arrival Date & Time</label>
            <input type="datetime-local"
                   name="arrivalTime" required>

            <label>Fare per Seat (₹)</label>
            <input type="number"
                   name="fare"
                   min="0"
                   step="0.01"
                   placeholder="Example: 150"
                   required>

            <button class="button primary-button" type="submit">
                Add Schedule
            </button>

        </form>
    </section>

    <!-- EXISTING SCHEDULES -->

    <section class="admin-card">
        <h2>Existing Schedules</h2>

        <p>
            Only scheduled journeys can be edited.
            Completed and cancelled journeys are read-only.
        </p>

        <div class="schedule-admin-list">

            <% if (schedules != null) {
                for (Schedule schedule : schedules) { %>

                <article class="admin-item-card">

                    <div class="admin-item-heading">
                        <div>
                            <span class="small-label">Train</span>

                            <h3>
                                <%= schedule.getTrainNumber() %>
                                -
                                <%= schedule.getTrainName() %>
                            </h3>
                        </div>

                        <span class="admin-status">
                            <%= schedule.getStatus() %>
                        </span>
                    </div>

                    <p class="admin-route-line">
                        <%= schedule.getRouteName() %>
                    </p>

                    <% if ("SCHEDULED".equals(schedule.getStatus())) { %>

                        <form class="schedule-edit-form" method="post"
                              action="${pageContext.request.contextPath}/admin/schedules">

                            <input type="hidden"
                                   name="action"
                                   value="update">

                            <input type="hidden"
                                   name="scheduleId"
                                   value="<%= schedule.getScheduleId() %>">

                            <div>
                                <label>Train</label>

                                <select name="trainId" required>
                                    <% if (trains != null) {
                                        for (Train train : trains) { %>

                                            <option
                                                value="<%= train.getTrainId() %>"
                                                <%= train.getTrainId() == schedule.getTrainId()
                                                        ? "selected" : "" %>>

                                                <%= train.getTrainNumber() %> -
                                                <%= train.getTrainName() %>

                                            </option>

                                    <%  }
                                       } %>
                                </select>
                            </div>

                            <div>
                                <label>Route</label>

                                <select name="routeId" required>
                                    <% if (routes != null) {
                                        for (Route route : routes) { %>

                                            <option
                                                value="<%= route.getRouteId() %>"
                                                <%= route.getRouteId() == schedule.getRouteId()
                                                        ? "selected" : "" %>>

                                                <%= route.getSourceStation() %> →
                                                <%= route.getDestinationStation() %>

                                            </option>

                                    <%  }
                                       } %>
                                </select>
                            </div>

                            <div>
                                <label>Departure</label>

                                <input type="datetime-local"
                                       name="departureTime"
                                       value="<%= schedule.getDepartureTime()
                                               .format(inputFormatter) %>"
                                       required>
                            </div>

                            <div>
                                <label>Arrival</label>

                                <input type="datetime-local"
                                       name="arrivalTime"
                                       value="<%= schedule.getArrivalTime()
                                               .format(inputFormatter) %>"
                                       required>
                            </div>

                            <div>
                                <label>Fare (₹)</label>

                                <input type="number"
                                       name="fare"
                                       min="0"
                                       step="0.01"
                                       value="<%= schedule.getFare() %>"
                                       required>
                            </div>

                            <div>
                                <label>Status</label>

                                <input type="text"
                                       value="SCHEDULED"
                                       readonly>

                                <input type="hidden"
                                       name="status"
                                       value="SCHEDULED">
                            </div>

                            <div class="schedule-edit-button">
                                <button class="button small-button primary-button"
                                        type="submit">
                                    Save Changes
                                </button>
                            </div>

                        </form>

                    <% } else { %>

                        <div class="schedule-readonly-message">
                            <p>
                                <strong><%= schedule.getStatus() %></strong>
                                — This journey cannot be edited.
                            </p>
                        </div>

                    <% } %>

                    <div class="schedule-summary-row">
                        <span>
                            <strong>Departure:</strong>
                            <%= schedule.getDepartureTime()
                                    .format(displayFormatter) %>
                        </span>

                        <span>
                            <strong>Arrival:</strong>
                            <%= schedule.getArrivalTime()
                                    .format(displayFormatter) %>
                        </span>

                        <span>
                            <strong>Fare:</strong>
                            ₹<%= String.format("%.2f", schedule.getFare()) %>
                        </span>
                    </div>

                </article>

            <%  }
               } %>

        </div>
    </section>

</main>

</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.railway.model.AdminSummary" %>
<%

    if (session.getAttribute("userId") == null
            || !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>

<header class="admin-header">
    <div class="admin-header-inner">
        <div>
            <h1>Digital Railway</h1>
            <p>Admin Portal</p>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="header-link">Logout</a>
    </div>
</header>

<main class="admin-container">

    <section class="admin-card">
        <h2>Admin Dashboard</h2>
        <p>Admin controls the railway network data used by the user portal.</p>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message"><%= request.getAttribute("error") %></div>
        <% } %>

        <div class="admin-menu-grid">
            <a class="admin-menu-card" href="${pageContext.request.contextPath}/admin/network">
                <h3>Stations & Routes</h3>
                <p>Add or update stations and routes used by train search.</p>
            </a>

            <a class="admin-menu-card" href="${pageContext.request.contextPath}/admin/trains">
                <h3>Train Management</h3>
                <p>Add and update trains.</p>
            </a>

            <a class="admin-menu-card" href="${pageContext.request.contextPath}/admin/schedules">
                <h3>Schedule Management</h3>
                <p>Add schedules and update their status.</p>
            </a>

            <a class="admin-menu-card" href="${pageContext.request.contextPath}/admin/seats">
                <h3>Seat Capacity Setup</h3>
                <p>Manage coaches and seat capacity.</p>
            </a>

            <a class="admin-menu-card" href="${pageContext.request.contextPath}/admin/bookings">
                <h3>Bookings & Revenue</h3>
                <p>Monitor bookings and revenue.</p>
            </a>

            <a class="admin-menu-card" href="${pageContext.request.contextPath}/admin/reports">
                <h3>Reports & Analytics</h3>
                <p>Review revenue and booking summaries.</p>
            </a>

            <a class="admin-menu-card" href="${pageContext.request.contextPath}/admin/logs">
                <h3>Server Activity Logs</h3>
                <p>View recent system activity.</p>
            </a>
        </div>

    </section>

</main>

</body>
</html>

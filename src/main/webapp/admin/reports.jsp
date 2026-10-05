<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.railway.model.RevenueReport" %>
<%@ page import="com.railway.model.RevenueRow" %>
<%@ page import="java.util.List" %>
<%
    if (!"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    RevenueReport report = (RevenueReport) request.getAttribute("report");
    List<RevenueRow> rows = (List<RevenueRow>) request.getAttribute("rows");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports & Analytics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<header class="admin-header">
    <div class="admin-header-inner">
        <div><h1>Digital Railway</h1><p>Reports & Analytics</p></div>
        <a class="header-link" href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</header>

<main class="admin-container">
    <div class="admin-nav">
        <a href="${pageContext.request.contextPath}/admin/dashboard">
            Dashboard
        </a>
        
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error-message"><%= request.getAttribute("error") %></div>
    <% } %>

    <% if (report != null) { %>
    <section class="admin-card">
        <h2>Overall Report</h2>
        <div class="stat-grid">
            <div class="stat-card"><span>Total Revenue</span><strong>₹<%= String.format("%.2f", report.getTotalRevenue()) %></strong></div>
            <div class="stat-card"><span>Successful Transactions</span><strong><%= report.getSuccessfulTransactions() %></strong></div>
            <div class="stat-card"><span>Confirmed Bookings</span><strong><%= report.getConfirmedBookings() %></strong></div>
            <div class="stat-card"><span>Cancelled Bookings</span><strong><%= report.getCancelledBookings() %></strong></div>
            <div class="stat-card"><span>Average Booking Amount</span><strong>₹<%= String.format("%.2f", report.getAverageBookingAmount()) %></strong></div>
        </div>
    </section>
    <% } %>

    <section class="admin-card">
        <h2>Revenue by Journey Date and Route</h2>
        <div class="table-wrapper">
            <table>
                <thead><tr><th>Journey Date</th><th>Route</th><th>Bookings</th><th>Revenue</th></tr></thead>
                <tbody>
                <% if (rows != null) { for (RevenueRow row : rows) { %>
                    <tr>
                        <td><%= row.getJourneyDate() %></td>
                        <td><%= row.getRoute() %></td>
                        <td><%= row.getBookings() %></td>
                        <td>₹<%= String.format("%.2f", row.getRevenue()) %></td>
                    </tr>
                <% }} %>
                </tbody>
            </table>
        </div>
    </section>
</main>
</body>
</html>

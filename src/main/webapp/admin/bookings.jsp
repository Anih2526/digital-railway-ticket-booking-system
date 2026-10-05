<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.railway.model.AdminSummary" %>
<%@ page import="com.railway.model.BookingSummary" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    if (!"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    AdminSummary summary =
            (AdminSummary) request.getAttribute("summary");
    List<BookingSummary> bookings =
            (List<BookingSummary>) request.getAttribute("bookings");

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bookings & Revenue</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<header class="admin-header">
    <div class="admin-header-inner">
        <div><h1>Digital Railway</h1><p>Bookings & Revenue</p></div>
        <a class="header-link" href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</header>

<main class="admin-container">
    <div class="admin-nav">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">
            Dashboard
        </a>
    </div>

    <section class="admin-card">
        <h2>Reports & Analytics</h2>

        <% if (summary != null) { %>
            <div class="stat-grid">
                <div class="stat-card">
                    <span>Total Bookings</span>
                    <strong><%= summary.getTotalBookings() %></strong>
                </div>
                <div class="stat-card">
                    <span>Confirmed Bookings</span>
                    <strong><%= summary.getConfirmedBookings() %></strong>
                </div>
                <div class="stat-card">
                    <span>Total Revenue</span>
                    <strong>₹<%= String.format("%.2f", summary.getTotalRevenue()) %></strong>
                </div>
                <div class="stat-card">
                    <span>Available Seats</span>
                    <strong><%= summary.getAvailableSeats() %></strong>
                </div>
            </div>
        <% } %>
    </section>

    <section class="admin-card">
        <h2>Recent Bookings</h2>

        <div class="table-wrapper">
            <table>
                <thead>
                <tr>
                    <th>Reference</th>
                    <th>Train</th>
                    <th>Route</th>
                    <th>Departure</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th>Payment</th>
                </tr>
                </thead>
                <tbody>
                <% if (bookings != null) {
                    for (BookingSummary booking : bookings) { %>
                    <tr>
                        <td><%= booking.getBookingReference() %></td>
                        <td><%= booking.getTrainNumber() %> - <%= booking.getTrainName() %></td>
                        <td><%= booking.getSource() %> → <%= booking.getDestination() %></td>
                        <td><%= booking.getDepartureTime().format(formatter) %></td>
                        <td>₹<%= String.format("%.2f", booking.getTotalAmount()) %></td>
                        <td><%= booking.getBookingStatus() %></td>
                        <td><%= booking.getPaymentStatus() %></td>
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

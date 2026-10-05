<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.railway.model.BookingSummary" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

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
    <title>Booking History</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user.css">
</head>
<body>

<header class="top-header">
    <div class="header-content header-row">
        <div>
            <h1>Digital Railway</h1>
            <p>Booking History</p>
        </div>
        <a class="header-link" href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</header>

<main class="page-container">
    <section class="content-card">

        <h2>My Booking History</h2>
        <p class="intro-text">All your railway bookings are shown here.</p>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (bookings == null || bookings.isEmpty()) { %>
            <div class="empty-state">
                <h3>No bookings yet</h3>
                <p>Search for a train and make your first booking.</p>
                <a class="button primary-button"
                   href="${pageContext.request.contextPath}/user/search.jsp">
                    Search Trains
                </a>
            </div>
        <% } else { %>

            <div class="history-list">
                <% for (BookingSummary booking : bookings) { %>

                    <article class="history-card">

                        <div class="history-top">
                            <div>
                                <span class="small-label">Booking Reference</span>
                                <strong><%= booking.getBookingReference() %></strong>
                            </div>

                            <span class="status-badge">
                                <%= booking.getBookingStatus() %>
                            </span>
                        </div>

                        <h3>
                            <%= booking.getTrainNumber() %>
                            -
                            <%= booking.getTrainName() %>
                        </h3>

                        <p>
                            <%= booking.getSource() %>
                            →
                            <%= booking.getDestination() %>
                        </p>

                        <div class="history-grid">
                            <div>
                                <span>Departure</span>
                                <strong>
                                    <%= booking.getDepartureTime().format(formatter) %>
                                </strong>
                            </div>

                            <div>
                                <span>Amount</span>
                                <strong>
                                    ₹<%= String.format("%.2f", booking.getTotalAmount()) %>
                                </strong>
                            </div>

                            <div>
                                <span>Payment</span>
                                <strong>
                                    <%= booking.getPaymentStatus() %>
                                </strong>
                            </div>
                        </div>

                        <div class="history-actions">

                            <a class="button primary-button"
                            href="${pageContext.request.contextPath}/bookingConfirmation?bookingReference=<%= booking.getBookingReference() %>">

                                View Ticket

                            </a>


                            <% if ("CONFIRMED".equals(
                                    booking.getBookingStatus())) { %>

                                <form method="post"
                                    action="${pageContext.request.contextPath}/cancelBooking"
                                    onsubmit="return confirm('Cancel this booking?');">

                                    <input
                                        type="hidden"
                                        name="bookingReference"
                                        value="<%= booking.getBookingReference() %>"
                                    >

                                    <button
                                        class="button cancel-button"
                                        type="submit">

                                        Cancel Booking

                                    </button>

                                </form>

                            <% } %>

                        </div>

                    </article>

                <% } %>
            </div>

        <% } %>

        <div class="button-group">
            <a class="button secondary-button"
               href="${pageContext.request.contextPath}/user/dashboard.jsp">
                Dashboard
            </a>
        </div>

    </section>
</main>

</body>
</html>

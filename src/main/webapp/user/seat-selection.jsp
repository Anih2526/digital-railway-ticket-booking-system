<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String scheduleId = request.getParameter("scheduleId");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Select Seats</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/seat.css">
</head>
<body>

<header class="top-header">
    <div class="header-content header-row">
        <div>
            <h1>Digital Railway</h1>
            <p>Select Seats</p>
        </div>

        <a class="header-link" href="${pageContext.request.contextPath}/user/dashboard.jsp">
            Dashboard
        </a>
    </div>
</header>

<main class="page-container">
    <section class="content-card">

        <div id="seatPage"
             data-schedule-id="<%= scheduleId == null ? "" : scheduleId %>">

            <h2>Select Your Seats</h2>

            <div class="seat-legend">
                <span><i class="legend available"></i> Available</span>
                <span><i class="legend selected"></i> Selected</span>
                <span><i class="legend booked"></i> Booked</span>
            </div>

            <div id="seatMessage"></div>
            <div id="seatContainer" class="coach-container"></div>

            <div class="booking-summary">
                <div>
                    <span>Selected Seats</span>
                    <strong id="selectedSeats">None</strong>
                </div>

                <div>
                    <span>Total</span>
                    <strong id="totalAmount">₹0.00</strong>
                </div>
            </div>

            <button type="button"
                    id="bookButton"
                    class="button primary-button full-button"
                    disabled>
                Book Selected Seats
            </button>

        </div>

    </section>
</main>

<script src="${pageContext.request.contextPath}/js/seats.js?v=4"></script>

</body>
</html>

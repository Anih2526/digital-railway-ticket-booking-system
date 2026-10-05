<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.railway.model.BookingDetails" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>

<%
    if (session.getAttribute("userId") == null) {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );

        return;
    }


    BookingDetails booking =
            (BookingDetails) request.getAttribute("booking");


    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a"
            );


    List<String> seats =
            booking.getSeatNames();


    boolean isConfirmed =
            "CONFIRMED".equals(
                    booking.getBookingStatus()
            );
%>


<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Ticket Confirmation
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">

</head>


<body>


<header class="top-header">

    <div class="header-content header-row">

        <div>

            <h1>
                Digital Railway
            </h1>

            <p>
                Ticket Confirmation
            </p>

        </div>


        <a
            class="header-link"
            href="${pageContext.request.contextPath}/logout">

            Logout

        </a>

    </div>

</header>


<main class="page-container">

    <section class="content-card">


        <% if (isConfirmed) { %>

            <div class="success-heading">

                <div class="success-mark">
                    ✓
                </div>

                <div>

                    <h2>
                        Booking Successful
                    </h2>

                    <p>
                        Your railway ticket has been booked successfully.
                    </p>

                </div>

            </div>

        <% } else { %>

            <div class="success-heading">

                <div class="success-mark cancelled-mark">
                    !
                </div>

                <div>

                    <h2>
                        Booking Cancelled
                    </h2>

                    <p>
                        This railway ticket has been cancelled.
                    </p>

                </div>

            </div>

        <% } %>


        <div class="ticket-details">


            <h3>
                Railway Ticket
            </h3>


            <div class="detail-row">

                <span>
                    Booking Reference
                </span>

                <strong>
                    <%= booking.getBookingReference() %>
                </strong>

            </div>


            <div class="detail-row">

                <span>
                    Train
                </span>

                <strong>

                    <%= booking.getTrainNumber() %>
                    -
                    <%= booking.getTrainName() %>

                </strong>

            </div>


            <div class="detail-row">

                <span>
                    Route
                </span>

                <strong>

                    <%= booking.getSource() %>
                    →
                    <%= booking.getDestination() %>

                </strong>

            </div>


            <div class="detail-row">

                <span>
                    Departure
                </span>

                <strong>

                    <%= booking.getDepartureTime()
                            .format(formatter) %>

                </strong>

            </div>


            <div class="detail-row">

                <span>
                    Arrival
                </span>

                <strong>

                    <%= booking.getArrivalTime()
                            .format(formatter) %>

                </strong>

            </div>


            <div class="detail-row">

                <span>
                    Seats
                </span>

                <strong>

                    <%
                        for (int i = 0;
                             i < seats.size();
                             i++) {

                            if (i > 0) {
                                out.print(", ");
                            }

                            out.print(
                                    seats.get(i)
                            );
                        }
                    %>

                </strong>

            </div>


            <div class="detail-row">

                <span>
                    Total Amount
                </span>

                <strong>

                    ₹<%= String.format(
                            "%.2f",
                            booking.getTotalAmount()
                       ) %>

                </strong>

            </div>


            <div class="detail-row">

                <span>
                    Booking Status
                </span>

                <strong>

                    <%= booking.getBookingStatus() %>

                </strong>

            </div>


            <div class="detail-row">

                <span>
                    Payment
                </span>

                <strong>

                    <%= booking.getPaymentStatus() %>
                    (SIMULATED)

                </strong>

            </div>


        </div>


        <div class="button-group">


            <a
                class="button primary-button"
                href="${pageContext.request.contextPath}/bookingHistory">

                Booking History

            </a>


            <a
                class="button primary-button"
                href="${pageContext.request.contextPath}/user/search.jsp">

                Search More Trains

            </a>


            <a
                class="button secondary-button"
                href="${pageContext.request.contextPath}/user/dashboard.jsp">

                Dashboard

            </a>


            <% if (isConfirmed) { %>

                <form
                    method="post"
                    action="${pageContext.request.contextPath}/cancelBooking"
                    onsubmit="return confirm('Are you sure you want to cancel this booking?');">

                    <input
                        type="hidden"
                        name="bookingReference"
                        value="<%= booking.getBookingReference() %>"
                    >

                    <button
                        type="submit"
                        class="button cancel-button">

                        Cancel Booking

                    </button>

                </form>

            <% } %>


        </div>


    </section>

</main>


</body>

</html>
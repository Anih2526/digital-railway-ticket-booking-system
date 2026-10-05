<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.railway.model.Train" %>
<%@ page import="com.railway.model.Coach" %>
<%@ page import="com.railway.model.CoachSeat" %>

<%@ page import="java.util.List" %>

<%
    if (!"ADMIN".equals(
            session.getAttribute("role"))) {

        response.sendRedirect(
                request.getContextPath()
                        + "/login.jsp"
        );

        return;
    }

    List<Train> trains =
            (List<Train>)
                    request.getAttribute("trains");

    List<Coach> coaches =
            (List<Coach>)
                    request.getAttribute("coaches");

    List<CoachSeat> coachSeats =
            (List<CoachSeat>)
                    request.getAttribute("coachSeats");
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Coach & Seat Management
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin.css">

</head>

<body>

<header class="admin-header">

    <div class="admin-header-inner">

        <div>

            <h1>
                Digital Railway
            </h1>

            <p>
                Coach & Seat Management
            </p>

        </div>

        <a class="header-link"
           href="${pageContext.request.contextPath}/logout">

            Logout

        </a>

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


    <!-- ========================= -->
    <!-- ADD COACH -->
    <!-- ========================= -->

    <section class="admin-card">

        <h2>
            Add Coach
        </h2>

        <p>
            Add a new coach and create its seats.
            The new seats are also added to existing
            scheduled trips for that train.
        </p>


        <form class="admin-form"
              method="post"
              action="${pageContext.request.contextPath}/admin/seats">

            <input type="hidden"
                   name="action"
                   value="addCoach">


            <label>
                Train
            </label>

            <select name="trainId"
                    required>

                <option value="">
                    Select Train
                </option>

                <% if (trains != null) {

                    for (Train train : trains) { %>

                        <option value="<%= train.getTrainId() %>">

                            <%= train.getTrainNumber() %>
                            -
                            <%= train.getTrainName() %>

                        </option>

                <%  }

                   } %>

            </select>


            <label>
                Coach Name
            </label>

            <input
                type="text"
                name="coachName"
                placeholder="Example: A3"
                required
            >


            <label>
                Seat Capacity
            </label>

            <input
                type="number"
                name="seatCapacity"
                min="1"
                max="100"
                placeholder="Example: 10"
                required
            >


            <button
                class="button primary-button"
                type="submit">

                Add Coach & Seats

            </button>

        </form>

    </section>


    <!-- ========================= -->
    <!-- EXISTING COACHES -->
    <!-- ========================= -->

    <section class="admin-card">

        <h2>
            Existing Coaches
        </h2>

        <p>
            The admin can change the coach name
            and seat capacity.
        </p>


        <div class="table-wrapper">

            <table>

                <thead>

                <tr>

                    <th>
                        Train
                    </th>

                    <th>
                        Coach
                    </th>

                    <th>
                        Capacity
                    </th>

                    <th>
                        Current Seats
                    </th>

                    <th>
                        Update
                    </th>

                </tr>

                </thead>


                <tbody>

                <% if (coaches != null) {

                    for (Coach coach : coaches) { %>

                    <tr>

                        <td>
                            <%= coach.getTrainNumber() %>
                        </td>


                        <td>

                            <form
                                class="inline-form"
                                method="post"
                                action="${pageContext.request.contextPath}/admin/seats">

                                <input
                                    type="hidden"
                                    name="action"
                                    value="updateCoach"
                                >

                                <input
                                    type="hidden"
                                    name="coachId"
                                    value="<%= coach.getCoachId() %>"
                                >

                                <input
                                    type="text"
                                    name="coachName"
                                    value="<%= coach.getCoachName() %>"
                                    required
                                >

                        </td>


                        <td>

                                <input
                                    type="number"
                                    name="seatCapacity"
                                    value="<%= coach.getSeatCapacity() %>"
                                    min="1"
                                    max="100"
                                    required
                                >

                        </td>


                        <td>
                            <%= coach.getSeatCount() %>
                        </td>


                        <td>

                                <button
                                    class="button primary-button small-button"
                                    type="submit">

                                    Update

                                </button>

                            </form>

                        </td>

                    </tr>

                <%  }

                   } %>

                </tbody>

            </table>

        </div>

    </section>


    <!-- ========================= -->
    <!-- INDIVIDUAL SEAT NUMBERS -->
    <!-- ========================= -->

    <section class="admin-card">

        <h2>
            Change Seat Numbers
        </h2>

        <p>
            Individual seat numbers can be changed
            when the seat has not been booked.
        </p>


        <div class="table-wrapper">

            <table>

                <thead>

                <tr>

                    <th>
                        Train
                    </th>

                    <th>
                        Coach
                    </th>

                    <th>
                        Current Seat
                    </th>

                    <th>
                        Booked On
                    </th>

                    <th>
                        New Seat Number
                    </th>

                    <th>
                        Update
                    </th>

                </tr>

                </thead>


                <tbody>

                <% if (coachSeats != null) {

                    for (CoachSeat seat :
                            coachSeats) { %>

                    <tr>

                        <td>
                            <%= seat.getTrainNumber() %>
                        </td>

                        <td>
                            <%= seat.getCoachName() %>
                        </td>

                        <td>
                            <strong>
                                <%= seat.getSeatNumber() %>
                            </strong>
                        </td>

                        <td>
                            <%= seat.getBookedCount() %>
                        </td>

                        <td>

                            <% if (seat.getBookedCount() == 0) { %>

                                <form
                                    class="inline-form"
                                    method="post"
                                    action="${pageContext.request.contextPath}/admin/seats">

                                    <input
                                        type="hidden"
                                        name="action"
                                        value="updateSeat"
                                    >

                                    <input
                                        type="hidden"
                                        name="seatId"
                                        value="<%= seat.getSeatId() %>"
                                    >

                                    <input
                                        type="number"
                                        name="seatNumber"
                                        value="<%= seat.getSeatNumber() %>"
                                        min="1"
                                        required
                                    >

                            <% } else { %>

                                <span>
                                    Cannot change
                                </span>

                            <% } %>

                        </td>

                        <td>

                            <% if (seat.getBookedCount() == 0) { %>

                                    <button
                                        class="button primary-button small-button"
                                        type="submit">

                                        Change

                                    </button>

                                </form>

                            <% } %>

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
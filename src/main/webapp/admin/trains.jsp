<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.railway.model.Train" %>
<%@ page import="java.util.List" %>

<%
    if (!"ADMIN".equals(session.getAttribute("role"))) {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );

        return;
    }

    List<Train> trains =
            (List<Train>) request.getAttribute("trains");
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Train Management</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin.css">

</head>

<body>

<header class="admin-header">

    <div class="admin-header-inner">

        <div>
            <h1>Digital Railway</h1>
            <p>Train Management</p>
        </div>

        <a class="header-link"
           href="${pageContext.request.contextPath}/logout">
            Logout
        </a>

    </div>

</header>


<main class="admin-container">


    <!-- Dashboard Button -->

    <div class="admin-nav">

        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">
            Dashboard
        </a>

    </div>


    <!-- Add Train -->

    <section class="admin-card">

        <h2>Add Train</h2>

        <% if (request.getAttribute("error") != null) { %>

            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>

        <% } %>


        <form class="admin-form"
              method="post"
              action="${pageContext.request.contextPath}/admin/trains">

            <input type="hidden"
                   name="action"
                   value="add">


            <label>Train Number</label>

            <input type="text"
                   name="trainNumber"
                   required>


            <label>Train Name</label>

            <input type="text"
                   name="trainName"
                   required>


            <button class="button primary-button"
                    type="submit">
                Add Train
            </button>

        </form>

    </section>


    <!-- Existing Trains -->

    <section class="admin-card">

        <h2>Existing Trains</h2>

        <div class="table-wrapper">

            <table>

                <thead>

                    <tr>

                        <th>Number</th>

                        <th>Name</th>

                        <th>Update</th>

                    </tr>

                </thead>


                <tbody>

                <% if (trains != null) {

                    for (Train train : trains) { %>


                    <tr>

                        <!-- Current Train Number -->

                        <td>
                            <%= train.getTrainNumber() %>
                        </td>


                        <!-- Current Train Name -->

                        <td>
                            <%= train.getTrainName() %>
                        </td>


                        <!-- Update Train -->

                        <td>

                            <form class="inline-form"
                                  method="post"
                                  action="${pageContext.request.contextPath}/admin/trains">

                                <input type="hidden"
                                       name="action"
                                       value="update">


                                <input type="hidden"
                                       name="trainId"
                                       value="<%= train.getTrainId() %>">


                                <!-- Change Train Number -->

                                <input type="text"
                                       name="trainNumber"
                                       value="<%= train.getTrainNumber() %>"
                                       required>


                                <!-- Change Train Name -->

                                <input type="text"
                                       name="trainName"
                                       value="<%= train.getTrainName() %>"
                                       required>


                                <button class="button small-button primary-button"
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


</main>

</body>

</html>
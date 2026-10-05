<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user.css">
</head>
<body>

<header class="top-header">
    <div class="header-content header-row">
        <div>
            <h1>Digital Railway</h1>
            <p>User Portal</p>
        </div>

        <a class="header-link" href="${pageContext.request.contextPath}/logout">
            Logout
        </a>
    </div>
</header>

<main class="page-container">

    <section class="content-card">
        <h2>Welcome, <%= session.getAttribute("fullName") %></h2>

        <p class="intro-text">
            Manage your railway ticket bookings from here.
        </p>

        <div class="profile-box">
            <div>
                <span>Email</span>
                <strong><%= session.getAttribute("email") %></strong>
            </div>
        </div>

        <div class="dashboard-grid">

            <a class="dashboard-card" href="${pageContext.request.contextPath}/user/search.jsp">
                <h3>Search Trains</h3>
                <p>Find trains and choose your seats.</p>
            </a>

            <a class="dashboard-card" href="${pageContext.request.contextPath}/bookingHistory">
                <h3>Booking History</h3>
                <p>View your previous railway bookings.</p>
            </a>

        </div>

    </section>

</main>

</body>
</html>

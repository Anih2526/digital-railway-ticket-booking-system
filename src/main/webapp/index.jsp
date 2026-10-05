<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Digital Railway Ticket Booking System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user.css">
</head>
<body>

<header class="top-header">
    <div class="header-content">
        <h1>Digital Railway</h1>
        <p>Ticket Booking System</p>
    </div>
</header>

<main class="page-container home-container">
    <section class="content-card home-card">
        <h2>Digital Railway Ticket Booking System</h2>
        <p class="intro-text">
            Search trains, select seats and book railway tickets.
        </p>

        <div class="button-group">
            <a class="button primary-button"
               href="${pageContext.request.contextPath}/login.jsp">Login</a>

            <a class="button primary-button"
               href="${pageContext.request.contextPath}/register.jsp">Register</a>

            <a class="button primary-button"
               href="${pageContext.request.contextPath}/login.jsp">Search Trains</a>
        </div>
    </section>
</main>

</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Digital Railway</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>

<div class="auth-page">
    <div class="auth-card">

        <h1>Login</h1>
        <p class="auth-subtitle">Access your railway booking account</p>

        <% if (request.getParameter("registered") != null) { %>
            <div class="success-message">
                Registration successful. Please login.
            </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="post">

            <label for="email">Email</label>
            <input type="email" id="email" name="email"
                   placeholder="Enter your email" required>

            <label for="password">Password</label>
            <input type="password" id="password" name="password"
                   placeholder="Enter your password" required>

            <button class="button primary-button" type="submit">
                Login
            </button>
        </form>

        <p class="auth-footer">
            New user?
            <a href="${pageContext.request.contextPath}/register.jsp">
                Create an account
            </a>
        </p>

        <a class="back-link" href="${pageContext.request.contextPath}/">
            ← Back to Home
        </a>

    </div>
</div>

</body>
</html>

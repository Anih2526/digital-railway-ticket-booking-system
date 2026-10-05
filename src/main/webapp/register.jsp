<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Digital Railway</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>

<div class="auth-page">
    <div class="auth-card">

        <h1>Create Account</h1>
        <p class="auth-subtitle">Register as a railway passenger</p>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/register" method="post">

            <label for="fullName">Full Name</label>
            <input type="text" id="fullName" name="fullName"
                   placeholder="Enter your full name" required>

            <label for="email">Email</label>
            <input type="email" id="email" name="email"
                   placeholder="Enter your email" required>

            <label for="password">Password</label>
            <input type="password" id="password" name="password"
                   placeholder="Create a password" minlength="4" required>

            <label for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword"
                   placeholder="Confirm your password" minlength="4" required>

            <button class="button primary-button" type="submit">
                Register
            </button>
        </form>

        <p class="auth-footer">
            Already registered?
            <a href="${pageContext.request.contextPath}/login.jsp">
                Login
            </a>
        </p>

        <a class="back-link" href="${pageContext.request.contextPath}/">
            ← Back to Home
        </a>

    </div>
</div>

</body>
</html>

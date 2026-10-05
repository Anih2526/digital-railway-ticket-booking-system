<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.railway.model.SystemLog" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    if (!"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<SystemLog> logs =
            (List<SystemLog>) request.getAttribute("logs");

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server Activity Logs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<header class="admin-header">
    <div class="admin-header-inner">
        <div><h1>Digital Railway</h1><p>Server Activity Logs</p></div>
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
        <h2>Recent Activity</h2>

        <div class="table-wrapper">
            <table>
                <thead>
                <tr>
                    <th>Time</th>
                    <th>User</th>
                    <th>Action</th>
                    <th>Message</th>
                </tr>
                </thead>
                <tbody>
                <% if (logs != null) {
                    for (SystemLog log : logs) { %>
                    <tr>
                        <td><%= log.getLogTime().format(formatter) %></td>
                        <td><%= log.getEmail() == null ? "SYSTEM" : log.getEmail() %></td>
                        <td><%= log.getAction() %></td>
                        <td><%= log.getMessage() %></td>
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

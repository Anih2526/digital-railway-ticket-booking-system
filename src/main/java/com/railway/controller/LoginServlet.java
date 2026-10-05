package com.railway.controller;

import com.railway.dao.LogDao;
import com.railway.dao.UserDao;
import com.railway.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {

            if (email == null || email.isBlank()
                    || password == null || password.isBlank()) {
                throw new Exception("Enter email and password.");
            }

            User user = userDao.findByEmailAndPassword(
                    email.trim(),
                    password
            );

            if (user == null) {
                throw new Exception("Invalid email or password.");
            }

            HttpSession session = request.getSession();

            session.setAttribute("userId", user.getUserId());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("role", user.getRole());

            logDao.createLog(
                    user.getUserId(),
                    "LOGIN",
                    "User logged in."
            );

            if ("ADMIN".equals(user.getRole())) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/admin/dashboard"
                );
            } else {
                response.sendRedirect(
                        request.getContextPath()
                                + "/user/dashboard.jsp"
                );
            }

        } catch (Exception exception) {

            request.setAttribute(
                    "error",
                    exception.getMessage()
            );

            request.getRequestDispatcher(
                    "/login.jsp"
            ).forward(request, response);
        }
    }
}

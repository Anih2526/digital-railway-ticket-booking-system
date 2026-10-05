package com.railway.controller;

import com.railway.dao.LogDao;
import com.railway.dao.UserDao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        try {

            if (fullName == null || fullName.isBlank()
                    || email == null || email.isBlank()
                    || password == null || password.isBlank()) {
                throw new Exception("All fields are required.");
            }

            if (password.length() < 4) {
                throw new Exception("Password must contain at least 4 characters.");
            }

            if (!password.equals(confirmPassword)) {
                throw new Exception("Passwords do not match.");
            }

            if (userDao.emailExists(email.trim())) {
                throw new Exception("An account with this email already exists.");
            }

            int userId = userDao.createUser(
                    fullName.trim(),
                    email.trim(),
                    password
            );

            logDao.createLog(
                    userId,
                    "REGISTRATION",
                    "New user account created."
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/login.jsp?registered=true"
            );

        } catch (Exception exception) {

            request.setAttribute(
                    "error",
                    exception.getMessage()
            );

            request.getRequestDispatcher(
                    "/register.jsp"
            ).forward(request, response);
        }
    }
}

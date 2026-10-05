package com.railway.controller;

import com.railway.dao.AdminDao;
import com.railway.model.AdminSummary;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final AdminDao adminDao = new AdminDao();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireAdmin(request, response)) {
            return;
        }

        try {
            AdminSummary summary = adminDao.getSummary();
            request.setAttribute("summary", summary);

            request.getRequestDispatcher(
                    "/admin/dashboard.jsp"
            ).forward(request, response);

        } catch (Exception exception) {

            exception.printStackTrace();
            request.setAttribute(
                    "error",
                    "Unable to load admin dashboard."
            );

            request.getRequestDispatcher(
                    "/admin/dashboard.jsp"
            ).forward(request, response);
        }
    }
}

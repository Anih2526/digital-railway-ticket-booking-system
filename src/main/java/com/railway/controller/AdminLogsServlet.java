package com.railway.controller;

import com.railway.dao.LogDao;
import com.railway.model.SystemLog;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/logs")
public class AdminLogsServlet extends HttpServlet {

    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireAdmin(request, response)) {
            return;
        }

        try {
            List<SystemLog> logs = logDao.getRecentLogs();
            request.setAttribute("logs", logs);
        } catch (Exception exception) {
            request.setAttribute("error", exception.getMessage());
        }

        request.getRequestDispatcher(
                "/admin/logs.jsp"
        ).forward(request, response);
    }
}

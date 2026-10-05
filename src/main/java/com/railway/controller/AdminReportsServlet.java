package com.railway.controller;

import com.railway.dao.ReportDao;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/reports")
public class AdminReportsServlet extends HttpServlet {

    private final ReportDao reportDao = new ReportDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireAdmin(request, response)) {
            return;
        }

        try {
            request.setAttribute("report", reportDao.getRevenueReport());
            request.setAttribute("rows", reportDao.getRevenueRows());
        } catch (Exception exception) {
            request.setAttribute("error", exception.getMessage());
        }

        request.getRequestDispatcher("/admin/reports.jsp")
                .forward(request, response);
    }
}

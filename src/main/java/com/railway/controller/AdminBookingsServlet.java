package com.railway.controller;

import com.railway.dao.AdminDao;
import com.railway.dao.BookingDao;
import com.railway.model.AdminSummary;
import com.railway.model.BookingSummary;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/bookings")
public class AdminBookingsServlet extends HttpServlet {

    private final AdminDao adminDao = new AdminDao();
    private final BookingDao bookingDao = new BookingDao();

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
            List<BookingSummary> bookings =
                    bookingDao.getAllBookings();

            request.setAttribute("summary", summary);
            request.setAttribute("bookings", bookings);

        } catch (Exception exception) {
            request.setAttribute("error", exception.getMessage());
        }

        request.getRequestDispatcher(
                "/admin/bookings.jsp"
        ).forward(request, response);
    }
}

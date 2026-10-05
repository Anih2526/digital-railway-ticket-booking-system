package com.railway.controller;

import com.railway.dao.BookingDao;
import com.railway.model.BookingSummary;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/bookingHistory")
public class BookingHistoryServlet extends HttpServlet {

    private final BookingDao bookingDao = new BookingDao();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireLogin(request, response)) {
            return;
        }

        try {

            int userId = (Integer) request.getSession(false)
                    .getAttribute("userId");

            List<BookingSummary> bookings =
                    bookingDao.getUserBookings(userId);

            request.setAttribute("bookings", bookings);

            request.getRequestDispatcher(
                    "/user/booking-history.jsp"
            ).forward(request, response);

        } catch (Exception exception) {

            exception.printStackTrace();

            request.setAttribute(
                    "error",
                    "Unable to load booking history."
            );

            request.getRequestDispatcher(
                    "/user/booking-history.jsp"
            ).forward(request, response);
        }
    }
}

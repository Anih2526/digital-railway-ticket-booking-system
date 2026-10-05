package com.railway.controller;

import com.railway.service.BookingService;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/cancelBooking")
public class CancelBookingServlet extends HttpServlet {

    private final BookingService bookingService =
            new BookingService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireLogin(
                request,
                response)) {

            return;
        }

        try {

            int userId =
                    (Integer) request
                            .getSession(false)
                            .getAttribute("userId");


            String bookingReference =
                    request.getParameter(
                            "bookingReference"
                    );


            bookingService.cancelBooking(
                    userId,
                    bookingReference
            );


            response.sendRedirect(
                    request.getContextPath()
                            + "/bookingHistory?cancelled=true"
            );

        } catch (Exception exception) {

            request.setAttribute(
                    "error",
                    exception.getMessage()
            );

            request.getRequestDispatcher(
                    "/user/booking-history.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }
}
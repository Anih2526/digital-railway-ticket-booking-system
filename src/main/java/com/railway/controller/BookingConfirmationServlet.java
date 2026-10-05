package com.railway.controller;

import com.railway.dao.BookingDao;
import com.railway.db.Database;
import com.railway.model.BookingDetails;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/bookingConfirmation")
public class BookingConfirmationServlet extends HttpServlet {

    private final BookingDao bookingDao = new BookingDao();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireLogin(request, response)) {
            return;
        }

        String reference =
                request.getParameter("bookingReference");

        if (reference == null || reference.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Booking reference is missing."
            );
            return;
        }

        int userId = (Integer) request.getSession(false)
                .getAttribute("userId");

        try (Connection connection = Database.getConnection()) {

            BookingDetails booking =
                    bookingDao.getBookingDetails(
                            connection,
                            userId,
                            reference
                    );

            if (booking == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Booking not found."
                );
                return;
            }

            List<String> seats =
                    bookingDao.getBookedSeatNames(
                            connection,
                            booking.getBookingId()
                    );

            BookingDetails completeBooking =
                    new BookingDetails(
                            booking.getBookingId(),
                            booking.getBookingReference(),
                            booking.getTrainNumber(),
                            booking.getTrainName(),
                            booking.getSource(),
                            booking.getDestination(),
                            booking.getDepartureTime(),
                            booking.getArrivalTime(),
                            booking.getTotalAmount(),
                            booking.getBookingStatus(),
                            booking.getPaymentStatus(),
                            booking.getBookedAt(),
                            seats
                    );

            request.setAttribute("booking", completeBooking);

            request.getRequestDispatcher(
                    "/user/booking-confirmation.jsp"
            ).forward(request, response);

        } catch (Exception exception) {

            exception.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load booking confirmation."
            );
        }
    }
}

package com.railway.controller;

import com.railway.model.BookingRequest;
import com.railway.service.BookingService;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/bookSeat")
public class BookingServlet extends HttpServlet {

    private final BookingService bookingService =
            new BookingService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireLogin(request, response)) {
            return;
        }

        try {

            HttpSession session = request.getSession(false);

            int userId = (Integer) session.getAttribute("userId");

            int scheduleId = Integer.parseInt(
                    request.getParameter("scheduleId")
            );

            String seatIdsText =
                    request.getParameter("seatIds");

            if (seatIdsText == null || seatIdsText.isBlank()) {
                throw new Exception("Please select at least one seat.");
            }

            List<Integer> seatIds = new ArrayList<>();

            for (String value : seatIdsText.split(",")) {
                int seatId = Integer.parseInt(value.trim());

                if (!seatIds.contains(seatId)) {
                    seatIds.add(seatId);
                }
            }

            BookingRequest bookingRequest =
                    new BookingRequest(
                            userId,
                            scheduleId,
                            seatIds
                    );

            String bookingReference =
                    bookingService.bookSeats(bookingRequest);

            String encodedReference =
                    URLEncoder.encode(
                            bookingReference,
                            StandardCharsets.UTF_8
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/bookingConfirmation?bookingReference="
                            + encodedReference
            );

        } catch (Exception exception) {

            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(
                    HttpServletResponse.SC_CONFLICT
            );

            String message = exception.getMessage();

            if (message == null || message.isBlank()) {
                message = "Unable to complete the booking.";
            }

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport"
                              content="width=device-width, initial-scale=1.0">
                        <title>Booking Failed</title>
                        <style>
                            * { box-sizing: border-box; }
                            body {
                                margin: 0;
                                font-family: Arial, sans-serif;
                                background: #f3f6fb;
                                color: #172033;
                            }
                            .page {
                                width: min(650px, calc(100% - 24px));
                                margin: 45px auto;
                            }
                            .card {
                                background: white;
                                padding: 30px;
                                border-radius: 14px;
                                box-shadow: 0 6px 20px rgba(0,0,0,0.08);
                                text-align: center;
                            }
                            h1 { color: #b42318; }
                            .message {
                                background: #fff1f0;
                                padding: 15px;
                                border-radius: 8px;
                                overflow-wrap: anywhere;
                            }
                            .button {
                                display: inline-flex;
                                align-items: center;
                                justify-content: center;
                                min-height: 46px;
                                padding: 0 24px;
                                margin-top: 20px;
                                background: #1769c2;
                                color: white;
                                border-radius: 8px;
                                text-decoration: none;
                                font-weight: 600;
                            }
                            @media (max-width: 600px) {
                                .page { margin: 25px auto; }
                                .card { padding: 24px 18px; }
                                .button { width: 100%; }
                            }
                        </style>
                    </head>
                    <body>
                        <main class="page">
                            <section class="card">
                                <h1>Booking Failed</h1>
                                <p class="message">{MESSAGE}</p>
                                <a class="button" href="javascript:history.back()">
                                    Go Back
                                </a>
                            </section>
                        </main>
                    </body>
                    </html>
                    """.replace("{MESSAGE}", escapeHtml(message));

            response.getWriter().println(html);
        }
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

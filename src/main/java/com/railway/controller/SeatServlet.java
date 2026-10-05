package com.railway.controller;

import com.railway.dao.ScheduleDao;
import com.railway.model.Seat;
import com.railway.service.SeatService;
import com.railway.util.AuthUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/getSeats")
public class SeatServlet extends HttpServlet {

    private final SeatService seatService = new SeatService();
    private final ScheduleDao scheduleDao = new ScheduleDao();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        if (!AuthUtil.requireLogin(request, response)) {
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            int scheduleId = Integer.parseInt(
                    request.getParameter("scheduleId")
            );

            List<Seat> seats =
                    seatService.getSeatsForSchedule(scheduleId);
            double fare = scheduleDao.getFare(scheduleId);

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < seats.size(); i++) {

                if (i > 0) {
                    json.append(",");
                }

                Seat seat = seats.get(i);

                json.append("{")
                    .append("\"scheduleSeatId\":")
                    .append(seat.getScheduleSeatId())

                    .append(",\"coachName\":\"")
                    .append(escape(seat.getCoachName()))

                    .append("\",\"seatNumber\":")
                    .append(seat.getSeatNumber())

                    .append(",\"status\":\"")
                    .append(escape(seat.getStatus()))

                    .append("\",\"fare\":")
                    .append(fare)

                    .append("}");
                    
            }

            json.append("]");
            response.getWriter().write(json.toString());

        } catch (Exception exception) {

            response.setStatus(500);
            response.getWriter().write(
                    "{\"error\":\"Unable to load seats\"}"
            );
        }
    }

    private String escape(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}

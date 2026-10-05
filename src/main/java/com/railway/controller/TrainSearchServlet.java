package com.railway.controller;

import com.railway.model.TrainSearchResult;
import com.railway.service.TrainService;
import com.railway.util.AuthUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/searchTrains")
public class TrainSearchServlet extends HttpServlet {

    private final TrainService trainService = new TrainService();

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a");

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

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

            String from = request.getParameter("from");
            String to = request.getParameter("to");
            String dateText = request.getParameter("date");
            String trainQuery = request.getParameter("train");

            if (dateText == null || dateText.isBlank()) {
                throw new Exception("Please choose a journey date.");
            }

            LocalDate journeyDate = LocalDate.parse(dateText);

            List<TrainSearchResult> trains =
                    trainService.searchTrains(
                            from,
                            to,
                            journeyDate,
                            trainQuery
                    );

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < trains.size(); i++) {

                if (i > 0) {
                    json.append(",");
                }

                TrainSearchResult train = trains.get(i);

                json.append("{")
                        .append("\"scheduleId\":")
                        .append(train.getScheduleId())
                        .append(",\"trainNumber\":\"")
                        .append(escape(train.getTrainNumber()))
                        .append("\",\"trainName\":\"")
                        .append(escape(train.getTrainName()))
                        .append("\",\"source\":\"")
                        .append(escape(train.getSource()))
                        .append("\",\"destination\":\"")
                        .append(escape(train.getDestination()))
                        .append("\",\"journeyDate\":\"")
                        .append(train.getJourneyDate())
                        .append("\",\"journeyDateText\":\"")
                        .append(escape(train.getJourneyDate().format(DATE_FORMAT)))
                        .append("\",\"departureTime\":\"")
                        .append(escape(formatTime(train.getDepartureTime())))
                        .append("\",\"arrivalTime\":\"")
                        .append(escape(formatTime(train.getArrivalTime())))
                        .append("\",\"durationMinutes\":")
                        .append(train.getDurationMinutes())
                        .append(",\"durationText\":\"")
                        .append(escape(formatDuration(train.getDurationMinutes())))
                        .append("\",\"fare\":")
                        .append(String.format(java.util.Locale.US, "%.2f", train.getFare()))
                        .append(",\"availableSeats\":")
                        .append(train.getAvailableSeats())
                        .append("}");
            }

            json.append("]");
            response.getWriter().write(json.toString());

        } catch (Exception exception) {

            response.setStatus(500);
            response.getWriter().write(
                    "{\"error\":\""
                            + escape(exception.getMessage())
                            + "\"}"
            );
        }
    }

    private String formatTime(LocalDateTime time) {
        return time.format(TIME_FORMAT);
    }

    private String formatDuration(long minutes) {
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (hours == 0) {
            return remainingMinutes + " min";
        }

        if (remainingMinutes == 0) {
            return hours + " hr";
        }

        return hours + " hr " + remainingMinutes + " min";
    }

    private String escape(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}

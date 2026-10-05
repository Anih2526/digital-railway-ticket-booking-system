package com.railway.controller;

import com.railway.dao.LogDao;
import com.railway.dao.RouteDao;
import com.railway.dao.ScheduleDao;
import com.railway.dao.TrainDao;
import com.railway.model.Route;
import com.railway.model.Schedule;
import com.railway.model.Train;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/admin/schedules")
public class ScheduleAdminServlet extends HttpServlet {

    private final ScheduleDao scheduleDao = new ScheduleDao();
    private final TrainDao trainDao = new TrainDao();
    private final RouteDao routeDao = new RouteDao();
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
            request.setAttribute("schedules", scheduleDao.getAllSchedules());
            request.setAttribute("trains", trainDao.getAllTrains());
            request.setAttribute("routes", routeDao.getAllRoutes());
        } catch (Exception exception) {
            request.setAttribute("error", exception.getMessage());
        }

        request.getRequestDispatcher(
                "/admin/schedules.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireAdmin(request, response)) {
            return;
        }

        try {
            int userId = (Integer) request.getSession(false)
                    .getAttribute("userId");

            String action = request.getParameter("action");

            if ("add".equals(action)) {

                int trainId = Integer.parseInt(
                        request.getParameter("trainId")
                );

                int routeId = Integer.parseInt(
                        request.getParameter("routeId")
                );

                LocalDateTime departure = LocalDateTime.parse(
                        request.getParameter("departureTime")
                );

                LocalDateTime arrival = LocalDateTime.parse(
                        request.getParameter("arrivalTime")
                );

                double fare = Double.parseDouble(
                        request.getParameter("fare")
                );

                scheduleDao.addSchedule(
                        trainId,
                        routeId,
                        departure,
                        arrival,
                        fare
                );

                logDao.createLog(
                        userId,
                        "SCHEDULE_ADD",
                        "Admin added a schedule with fare."
                );

            } else if ("update".equals(action)) {

                int scheduleId = Integer.parseInt(
                        request.getParameter("scheduleId")
                );

                int trainId = Integer.parseInt(
                        request.getParameter("trainId")
                );

                int routeId = Integer.parseInt(
                        request.getParameter("routeId")
                );

                LocalDateTime departure = LocalDateTime.parse(
                        request.getParameter("departureTime")
                );

                LocalDateTime arrival = LocalDateTime.parse(
                        request.getParameter("arrivalTime")
                );

                double fare = Double.parseDouble(
                        request.getParameter("fare")
                );

                String status = request.getParameter("status");

                scheduleDao.updateSchedule(
                        scheduleId,
                        trainId,
                        routeId,
                        departure,
                        arrival,
                        fare,
                        status
                );

                logDao.createLog(
                        userId,
                        "SCHEDULE_UPDATE",
                        "Admin updated schedule train, route, timing, fare or status."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/schedules"
            );

        } catch (Exception exception) {

            request.setAttribute("error", exception.getMessage());
            doGet(request, response);
        }
    }
}

package com.railway.controller;

import com.railway.dao.LogDao;
import com.railway.dao.RouteDao;
import com.railway.dao.StationDao;
import com.railway.model.Route;
import com.railway.model.Station;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/network")
public class NetworkAdminServlet extends HttpServlet {

    private final StationDao stationDao = new StationDao();
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
            List<Station> stations = stationDao.getAllStations();
            List<Route> routes = routeDao.getAllRoutes();

            request.setAttribute("stations", stations);
            request.setAttribute("routes", routes);

        } catch (Exception exception) {
            request.setAttribute("error", exception.getMessage());
        }

        request.getRequestDispatcher(
                "/admin/network.jsp"
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

            if ("addStation".equals(action)) {

                stationDao.addStation(
                        request.getParameter("stationCode"),
                        request.getParameter("stationName")
                );

                logDao.createLog(
                        userId,
                        "STATION_ADD",
                        "Admin added a station."
                );

            } else if ("updateStation".equals(action)) {

                stationDao.updateStation(
                        Integer.parseInt(request.getParameter("stationId")),
                        request.getParameter("stationCode"),
                        request.getParameter("stationName")
                );

                logDao.createLog(
                        userId,
                        "STATION_UPDATE",
                        "Admin updated a station."
                );

            } else if ("addRoute".equals(action)) {

                routeDao.addRoute(
                        request.getParameter("routeName"),
                        Integer.parseInt(
                                request.getParameter("sourceStationId")
                        ),
                        Integer.parseInt(
                                request.getParameter("destinationStationId")
                        )
                );

                logDao.createLog(
                        userId,
                        "ROUTE_ADD",
                        "Admin added a route."
                );

            } else if ("updateRoute".equals(action)) {

                routeDao.updateRoute(
                        Integer.parseInt(request.getParameter("routeId")),
                        request.getParameter("routeName"),
                        Integer.parseInt(
                                request.getParameter("sourceStationId")
                        ),
                        Integer.parseInt(
                                request.getParameter("destinationStationId")
                        )
                );

                logDao.createLog(
                        userId,
                        "ROUTE_UPDATE",
                        "Admin updated a route."
                );
            }

            response.sendRedirect(
                    request.getContextPath() + "/admin/network"
            );

        } catch (Exception exception) {

            request.setAttribute("error", exception.getMessage());
            doGet(request, response);
        }
    }
}

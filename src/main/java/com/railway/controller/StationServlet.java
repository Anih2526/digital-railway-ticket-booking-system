package com.railway.controller;

import com.railway.dao.StationDao;
import com.railway.util.AuthUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/stations")
public class StationServlet extends HttpServlet {

    private final StationDao stationDao = new StationDao();

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

            List<String> stations = stationDao.getAllStationNames();

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < stations.size(); i++) {

                if (i > 0) {
                    json.append(",");
                }

                json.append("\"")
                        .append(escape(stations.get(i)))
                        .append("\"");
            }

            json.append("]");
            response.getWriter().write(json.toString());

        } catch (Exception exception) {

            response.setStatus(500);
            response.getWriter().write(
                    "{\"error\":\"Unable to load stations\"}"
            );
        }
    }

    private String escape(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}

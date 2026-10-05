package com.railway.controller;

import com.railway.dao.LogDao;
import com.railway.dao.TrainDao;
import com.railway.model.Train;
import com.railway.service.TrainService;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/trains")
public class TrainAdminServlet extends HttpServlet {

    private final TrainDao trainDao = new TrainDao();
    private final TrainService trainService = new TrainService();
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

            List<Train> trains =
                    trainDao.getAllTrains();

            request.setAttribute(
                    "trains",
                    trains
            );

        } catch (Exception exception) {

            request.setAttribute(
                    "error",
                    exception.getMessage()
            );
        }

        request.getRequestDispatcher(
                "/admin/trains.jsp"
        ).forward(
                request,
                response
        );
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

            String action =
                    request.getParameter("action");

            int userId =
                    (Integer) request
                            .getSession(false)
                            .getAttribute("userId");


            // ADD TRAIN
            if ("add".equals(action)) {

                trainService.addTrain(
                        request.getParameter(
                                "trainNumber"
                        ),
                        request.getParameter(
                                "trainName"
                        )
                );

                logDao.createLog(
                        userId,
                        "TRAIN_ADD",
                        "Admin added a train."
                );
            }


            // UPDATE TRAIN
            else if ("update".equals(action)) {

                trainService.updateTrain(
                        Integer.parseInt(
                                request.getParameter(
                                        "trainId"
                                )
                        ),
                        request.getParameter(
                                "trainNumber"
                        ),
                        request.getParameter(
                                "trainName"
                        )
                );

                logDao.createLog(
                        userId,
                        "TRAIN_UPDATE",
                        "Admin updated train number and name."
                );
            }


            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/trains"
            );

        } catch (Exception exception) {

            request.setAttribute(
                    "error",
                    exception.getMessage()
            );

            doGet(
                    request,
                    response
            );
        }
    }
}
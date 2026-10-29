package com.railway.controller;

import com.railway.dao.LogDao;
import com.railway.dao.SeatDao;
import com.railway.dao.TrainDao;
import com.railway.util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/seats")
public class SeatAdminServlet extends HttpServlet {

    private final SeatDao seatDao =
            new SeatDao();

    private final TrainDao trainDao =
            new TrainDao();

    private final LogDao logDao =
            new LogDao();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireAdmin(
                request,
                response)) {

            return;
        }

        try {

            request.setAttribute(
                    "trains",
                    trainDao.getAllTrains()
            );

            request.setAttribute(
                    "coaches",
                    seatDao.getAllCoaches()
            );

            request.setAttribute(
                    "coachSeats",
                    seatDao.getAllCoachSeats()
            );

        } catch (Exception exception) {

            request.setAttribute(
                    "error",
                    exception.getMessage()
            );
        }

        request.getRequestDispatcher(
                "/admin/seats.jsp"
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

        if (!AuthUtil.requireAdmin(
                request,
                response)) {

            return;
        }

        try {

            String action =
                    request.getParameter("action");

            int userId =
                    (Integer) request
                            .getSession(false)
                            .getAttribute("userId");


            if ("addCoach".equals(action)) {

                int trainId =
                        Integer.parseInt(
                                request.getParameter(
                                        "trainId"
                                )
                        );

                String coachName =
                        request.getParameter(
                                "coachName"
                        );

                int seatCapacity =
                        Integer.parseInt(
                                request.getParameter(
                                        "seatCapacity"
                                )
                        );


                seatDao.addCoachAndSeats(
                        trainId,
                        coachName,
                        seatCapacity
                );


                logDao.createLog(
                        userId,
                        "COACH_ADD",
                        "Admin added a coach."
                );


            } else if ("updateCoach".equals(action)) {

                int coachId =
                        Integer.parseInt(
                                request.getParameter(
                                        "coachId"
                                )
                        );

                String coachName =
                        request.getParameter(
                                "coachName"
                        );

                int seatCapacity =
                        Integer.parseInt(
                                request.getParameter(
                                        "seatCapacity"
                                )
                        );


                seatDao.updateCoachAndSeats(
                        coachId,
                        coachName,
                        seatCapacity
                );


                logDao.createLog(
                        userId,
                        "COACH_UPDATE",
                        "Admin updated coach details and seat capacity."
                );


            } else if ("updateSeat".equals(action)) {

                int seatId =
                        Integer.parseInt(
                                request.getParameter(
                                        "seatId"
                                )
                        );

                int seatNumber =
                        Integer.parseInt(
                                request.getParameter(
                                        "seatNumber"
                                )
                        );


                seatDao.updateSeatNumber(
                        seatId,
                        seatNumber
                );


                logDao.createLog(
                        userId,
                        "SEAT_UPDATE",
                        "Admin changed a seat number."
                );
            }


            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/seats"
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
package com.railway.service;

import com.railway.dao.BookingDao;
import com.railway.dao.LogDao;
import com.railway.db.Database;
import com.railway.model.BookingRequest;

import java.sql.Connection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BookingService {

    /*
     * One lock is used for each schedule.
     * This keeps concurrent booking requests safe.
     */
    private final ConcurrentHashMap<Integer, Object> scheduleLocks =
            new ConcurrentHashMap<>();

    private final BookingDao bookingDao = new BookingDao();
    private final LogDao logDao = new LogDao();


    public String bookSeats(BookingRequest request) throws Exception {

        Object lock = scheduleLocks.computeIfAbsent(
                request.getScheduleId(),
                id -> new Object()
        );

        synchronized (lock) {
            return processBooking(request);
        }
    }

        public void cancelBooking(
                int userId,
                String bookingReference)
                throws Exception {

        if (bookingReference == null
                || bookingReference.isBlank()) {

                throw new Exception(
                        "Booking reference is required."
                );
        }


        /*
        * Find the schedule first so cancellation
        * uses the same schedule lock as booking.
        */
        int scheduleId =
                bookingDao.getScheduleIdForBooking(
                        userId,
                        bookingReference
                );


        Object lock =
                scheduleLocks.computeIfAbsent(
                        scheduleId,
                        id -> new Object()
                );


        synchronized (lock) {

                try (Connection connection =
                        Database.getConnection()) {

                connection.setAutoCommit(false);

                try {

                        bookingDao.cancelBookingAndReleaseSeats(
                                connection,
                                userId,
                                bookingReference
                        );


                        logDao.createLog(
                                connection,
                                userId,
                                "BOOKING_CANCEL",
                                "Booking "
                                        + bookingReference
                                        + " was cancelled."
                        );


                        connection.commit();

                } catch (Exception exception) {

                        connection.rollback();

                        throw exception;
                }
                }
        }
        }

    private String processBooking(BookingRequest request) throws Exception {

        if (request.getSeatIds() == null
                || request.getSeatIds().isEmpty()) {
            throw new Exception("Please select at least one seat.");
        }

        try (Connection connection = Database.getConnection()) {

            connection.setAutoCommit(false);

            try {

                /*
                 * Check every requested seat while holding
                 * the database row lock.
                 */
                for (Integer seatId : request.getSeatIds()) {

                    boolean available =
                            bookingDao.areSeatsAvailable(
                                    connection,
                                    request.getScheduleId(),
                                    seatId
                            );

                    if (!available) {
                        throw new Exception(
                                "One or more selected seats are already booked. "
                                + "Please choose different seats."
                        );
                    }
                }

                double fare =
                        bookingDao.getFareForSchedule(
                                connection,
                                request.getScheduleId()
                        );

                double totalAmount =
                        fare * request.getSeatIds().size();

                String bookingReference = generateBookingReference();

                int bookingId = bookingDao.createBooking(
                        connection,
                        request.getUserId(),
                        request.getScheduleId(),
                        bookingReference,
                        totalAmount
                );

                for (Integer seatId : request.getSeatIds()) {

                    bookingDao.addBookingSeat(
                            connection,
                            bookingId,
                            seatId,
                            fare
                    );

                    bookingDao.markSeatBooked(
                            connection,
                            seatId
                    );
                }

                /*
                 * Payment is simulated for this project.
                 */
                bookingDao.createTransaction(
                        connection,
                        bookingId,
                        totalAmount
                );

                logDao.createLog(
                        connection,
                        request.getUserId(),
                        "BOOKING",
                        "Booking " + bookingReference
                                + " confirmed for "
                                + request.getSeatIds().size()
                                + " seat(s)."
                );

                connection.commit();

                return bookingReference;

            } catch (Exception exception) {

                connection.rollback();
                throw exception;
            }
        }
    }

    private String generateBookingReference() {

        String id = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();

        return "RLY-" + id;
    }
}

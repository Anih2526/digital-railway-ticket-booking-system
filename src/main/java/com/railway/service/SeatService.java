package com.railway.service;

import com.railway.dao.SeatDao;
import com.railway.model.Coach;
import com.railway.model.Seat;

import java.util.List;

public class SeatService {

    private final SeatDao seatDao = new SeatDao();

    public List<Seat> getSeatsForSchedule(int scheduleId)
            throws Exception {
        return seatDao.getSeatsForSchedule(scheduleId);
    }

    public List<Coach> getAllCoaches() throws Exception {
        return seatDao.getAllCoaches();
    }

    public void addCoach(
            int trainId,
            String coachName,
            int seatCapacity) throws Exception {

        if (coachName == null || coachName.isBlank()) {
            throw new Exception("Coach name is required.");
        }

        if (seatCapacity < 1 || seatCapacity > 100) {
            throw new Exception("Seat capacity must be between 1 and 100.");
        }

        seatDao.addCoachAndSeats(
                trainId,
                coachName.trim().toUpperCase(),
                seatCapacity
        );
    }
}

package com.railway.model;

import java.util.List;

public class BookingRequest {

    private int userId;
    private int scheduleId;
    private List<Integer> seatIds;

    public BookingRequest(int userId,
                          int scheduleId,
                          List<Integer> seatIds) {
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.seatIds = seatIds;
    }

    public int getUserId() {
        return userId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public List<Integer> getSeatIds() {
        return seatIds;
    }
}

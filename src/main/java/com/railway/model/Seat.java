package com.railway.model;

public class Seat {

    private int scheduleSeatId;
    private String coachName;
    private int seatNumber;
    private String status;

    public Seat(int scheduleSeatId,
                String coachName,
                int seatNumber,
                String status) {
        this.scheduleSeatId = scheduleSeatId;
        this.coachName = coachName;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public int getScheduleSeatId() {
        return scheduleSeatId;
    }

    public String getCoachName() {
        return coachName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getStatus() {
        return status;
    }
}

package com.railway.model;

public class CoachSeat {

    private int seatId;
    private int coachId;
    private String trainNumber;
    private String coachName;
    private int seatNumber;
    private int bookedCount;

    public CoachSeat(
            int seatId,
            int coachId,
            String trainNumber,
            String coachName,
            int seatNumber,
            int bookedCount) {

        this.seatId = seatId;
        this.coachId = coachId;
        this.trainNumber = trainNumber;
        this.coachName = coachName;
        this.seatNumber = seatNumber;
        this.bookedCount = bookedCount;
    }

    public int getSeatId() {
        return seatId;
    }

    public int getCoachId() {
        return coachId;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getCoachName() {
        return coachName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public int getBookedCount() {
        return bookedCount;
    }
}
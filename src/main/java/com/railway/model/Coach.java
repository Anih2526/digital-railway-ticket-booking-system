package com.railway.model;

public class Coach {

    private int coachId;
    private int trainId;
    private String trainNumber;
    private String coachName;
    private int seatCapacity;
    private int seatCount;

    public Coach(int coachId,
                  int trainId,
                  String trainNumber,
                  String coachName,
                  int seatCapacity,
                  int seatCount) {
        this.coachId = coachId;
        this.trainId = trainId;
        this.trainNumber = trainNumber;
        this.coachName = coachName;
        this.seatCapacity = seatCapacity;
        this.seatCount = seatCount;
    }

    public int getCoachId() { return coachId; }
    public int getTrainId() { return trainId; }
    public String getTrainNumber() { return trainNumber; }
    public String getCoachName() { return coachName; }
    public int getSeatCapacity() { return seatCapacity; }
    public int getSeatCount() { return seatCount; }
}

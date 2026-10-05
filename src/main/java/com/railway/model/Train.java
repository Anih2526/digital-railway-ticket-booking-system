package com.railway.model;

public class Train {

    private int trainId;
    private String trainNumber;
    private String trainName;
    private int totalCapacity;

    public Train(int trainId, String trainNumber,
                 String trainName, int totalCapacity) {
        this.trainId = trainId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.totalCapacity = totalCapacity;
    }

    public int getTrainId() {
        return trainId;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }
}

package com.railway.model;

import java.time.LocalDateTime;

public class Schedule {

    private int scheduleId;
    private int trainId;
    private int routeId;
    private String trainNumber;
    private String trainName;
    private String routeName;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double fare;
    private String status;

    public Schedule(
            int scheduleId,
            int trainId,
            int routeId,
            String trainNumber,
            String trainName,
            String routeName,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            double fare,
            String status) {

        this.scheduleId = scheduleId;
        this.trainId = trainId;
        this.routeId = routeId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.routeName = routeName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.status = status;
    }

    public int getScheduleId() { return scheduleId; }
    public int getTrainId() { return trainId; }
    public int getRouteId() { return routeId; }
    public String getTrainNumber() { return trainNumber; }
    public String getTrainName() { return trainName; }
    public String getRouteName() { return routeName; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public double getFare() { return fare; }
    public String getStatus() { return status; }
}

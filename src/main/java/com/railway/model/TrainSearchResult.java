package com.railway.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;

public class TrainSearchResult {

    private int scheduleId;
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private LocalDate journeyDate;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private long durationMinutes;
    private double fare;
    private int availableSeats;

    public TrainSearchResult(
            int scheduleId,
            String trainNumber,
            String trainName,
            String source,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            double fare,
            int availableSeats) {

        this.scheduleId = scheduleId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.journeyDate = departureTime.toLocalDate();
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.durationMinutes = Duration.between(
                departureTime,
                arrivalTime
        ).toMinutes();
        this.fare = fare;
        this.availableSeats = availableSeats;
    }

    public int getScheduleId() { return scheduleId; }
    public String getTrainNumber() { return trainNumber; }
    public String getTrainName() { return trainName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public long getDurationMinutes() { return durationMinutes; }
    public double getFare() { return fare; }
    public int getAvailableSeats() { return availableSeats; }
}

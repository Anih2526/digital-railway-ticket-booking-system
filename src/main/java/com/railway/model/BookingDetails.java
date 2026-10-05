package com.railway.model;

import java.time.LocalDateTime;
import java.util.List;

public class BookingDetails {

    private int bookingId;
    private String bookingReference;
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double totalAmount;
    private String bookingStatus;
    private String paymentStatus;
    private LocalDateTime bookedAt;
    private List<String> seatNames;

    public BookingDetails(
            int bookingId,
            String bookingReference,
            String trainNumber,
            String trainName,
            String source,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            double totalAmount,
            String bookingStatus,
            String paymentStatus,
            LocalDateTime bookedAt,
            List<String> seatNames) {

        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.bookedAt = bookedAt;
        this.seatNames = seatNames;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public List<String> getSeatNames() {
        return seatNames;
    }
}

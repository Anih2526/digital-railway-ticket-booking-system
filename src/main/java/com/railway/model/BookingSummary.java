package com.railway.model;

import java.time.LocalDateTime;

public class BookingSummary {

    private int bookingId;
    private String bookingReference;
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private double totalAmount;
    private String bookingStatus;
    private String paymentStatus;
    private LocalDateTime bookedAt;

    public BookingSummary(
            int bookingId,
            String bookingReference,
            String trainNumber,
            String trainName,
            String source,
            String destination,
            LocalDateTime departureTime,
            double totalAmount,
            String bookingStatus,
            String paymentStatus,
            LocalDateTime bookedAt) {

        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.bookedAt = bookedAt;
    }

    public int getBookingId() { return bookingId; }
    public String getBookingReference() { return bookingReference; }
    public String getTrainNumber() { return trainNumber; }
    public String getTrainName() { return trainName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public double getTotalAmount() { return totalAmount; }
    public String getBookingStatus() { return bookingStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getBookedAt() { return bookedAt; }
}

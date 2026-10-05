CREATE DATABASE IF NOT EXISTS railway_ticket_system
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE railway_ticket_system;

DROP TABLE IF EXISTS system_logs;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS booking_seats;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS schedule_seats;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS coaches;
DROP TABLE IF EXISTS trains;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS stations;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stations (
    station_id INT PRIMARY KEY AUTO_INCREMENT,
    station_code VARCHAR(20) NOT NULL UNIQUE,
    station_name VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE routes (
    route_id INT PRIMARY KEY AUTO_INCREMENT,
    route_name VARCHAR(160) NOT NULL,
    source_station_id INT NOT NULL,
    destination_station_id INT NOT NULL,
    CONSTRAINT fk_route_source
        FOREIGN KEY (source_station_id) REFERENCES stations(station_id),
    CONSTRAINT fk_route_destination
        FOREIGN KEY (destination_station_id) REFERENCES stations(station_id)
);

CREATE TABLE trains (
    train_id INT PRIMARY KEY AUTO_INCREMENT,
    train_number VARCHAR(20) NOT NULL UNIQUE,
    train_name VARCHAR(120) NOT NULL,
    total_capacity INT NOT NULL DEFAULT 0
);

CREATE TABLE coaches (
    coach_id INT PRIMARY KEY AUTO_INCREMENT,
    train_id INT NOT NULL,
    coach_name VARCHAR(30) NOT NULL,
    seat_capacity INT NOT NULL,
    UNIQUE (train_id, coach_name),
    CONSTRAINT fk_coach_train
        FOREIGN KEY (train_id) REFERENCES trains(train_id)
        ON DELETE CASCADE
);

CREATE TABLE seats (
    seat_id INT PRIMARY KEY AUTO_INCREMENT,
    coach_id INT NOT NULL,
    seat_number INT NOT NULL,
    UNIQUE (coach_id, seat_number),
    CONSTRAINT fk_seat_coach
        FOREIGN KEY (coach_id) REFERENCES coaches(coach_id)
        ON DELETE CASCADE
);

CREATE TABLE schedules (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    train_id INT NOT NULL,
    route_id INT NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    fare DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED')
        NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT fk_schedule_train
        FOREIGN KEY (train_id) REFERENCES trains(train_id),
    CONSTRAINT fk_schedule_route
        FOREIGN KEY (route_id) REFERENCES routes(route_id)
);

CREATE TABLE schedule_seats (
    schedule_seat_id INT PRIMARY KEY AUTO_INCREMENT,
    schedule_id INT NOT NULL,
    seat_id INT NOT NULL,
    status ENUM('AVAILABLE', 'BOOKED') NOT NULL DEFAULT 'AVAILABLE',
    UNIQUE (schedule_id, seat_id),
    CONSTRAINT fk_schedule_seat_schedule
        FOREIGN KEY (schedule_id) REFERENCES schedules(schedule_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_schedule_seat_seat
        FOREIGN KEY (seat_id) REFERENCES seats(seat_id)
        ON DELETE CASCADE
);

CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_reference VARCHAR(30) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    schedule_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    booked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_booking_schedule
        FOREIGN KEY (schedule_id) REFERENCES schedules(schedule_id)
);

CREATE TABLE booking_seats (
    booking_seat_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    schedule_seat_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_booking_seat_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_booking_seat_schedule_seat
        FOREIGN KEY (schedule_seat_id) REFERENCES schedule_seats(schedule_seat_id)
);

CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status ENUM('SUCCESS', 'FAILED') NOT NULL,
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
        ON DELETE CASCADE
);

CREATE TABLE system_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL,
    action VARCHAR(80) NOT NULL,
    message VARCHAR(255) NOT NULL,
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE SET NULL
);

CREATE INDEX idx_schedule_search
    ON schedules(route_id, departure_time, status);

CREATE INDEX idx_booking_user
    ON bookings(user_id, booked_at);

CREATE INDEX idx_logs_time
    ON system_logs(log_time);

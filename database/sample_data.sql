USE railway_ticket_system;

INSERT IGNORE INTO users (full_name, email, password, role)
VALUES
('Test Passenger', 'user@gmail.com', '1234', 'USER'),
('System Administrator', 'admin@gmail.com', '1234', 'ADMIN');

INSERT IGNORE INTO stations (station_code, station_name) VALUES
('TVC', 'Thiruvananthapuram'),
('KTYM', 'Kottayam'),
('ERS', 'Ernakulam'),
('QLN', 'Kollam'),
('CLT', 'Kozhikode'),
('CAN', 'Kannur');

INSERT INTO routes (route_name, source_station_id, destination_station_id)
SELECT 'Thiruvananthapuram - Ernakulam', s1.station_id, s2.station_id
FROM stations s1 CROSS JOIN stations s2
WHERE s1.station_code='TVC' AND s2.station_code='ERS'
  AND NOT EXISTS (
      SELECT 1 FROM routes r
      WHERE r.source_station_id=s1.station_id
        AND r.destination_station_id=s2.station_id
  );

INSERT INTO routes (route_name, source_station_id, destination_station_id)
SELECT 'Kollam - Kottayam', s1.station_id, s2.station_id
FROM stations s1 CROSS JOIN stations s2
WHERE s1.station_code='QLN' AND s2.station_code='KTYM'
  AND NOT EXISTS (
      SELECT 1 FROM routes r
      WHERE r.source_station_id=s1.station_id
        AND r.destination_station_id=s2.station_id
  );

INSERT INTO routes (route_name, source_station_id, destination_station_id)
SELECT 'Kottayam - Ernakulam', s1.station_id, s2.station_id
FROM stations s1 CROSS JOIN stations s2
WHERE s1.station_code='KTYM' AND s2.station_code='ERS'
  AND NOT EXISTS (
      SELECT 1 FROM routes r
      WHERE r.source_station_id=s1.station_id
        AND r.destination_station_id=s2.station_id
  );

INSERT IGNORE INTO trains (train_number, train_name, total_capacity) VALUES
('12001', 'Railway Express', 20),
('12002', 'Coastal Express', 12),
('12003', 'Malabar Express', 20);

INSERT IGNORE INTO coaches (train_id, coach_name, seat_capacity)
SELECT train_id, 'A1', 10 FROM trains WHERE train_number='12001';
INSERT IGNORE INTO coaches (train_id, coach_name, seat_capacity)
SELECT train_id, 'A2', 10 FROM trains WHERE train_number='12001';
INSERT IGNORE INTO coaches (train_id, coach_name, seat_capacity)
SELECT train_id, 'B1', 6 FROM trains WHERE train_number='12002';
INSERT IGNORE INTO coaches (train_id, coach_name, seat_capacity)
SELECT train_id, 'B2', 6 FROM trains WHERE train_number='12002';
INSERT IGNORE INTO coaches (train_id, coach_name, seat_capacity)
SELECT train_id, 'C1', 10 FROM trains WHERE train_number='12003';
INSERT IGNORE INTO coaches (train_id, coach_name, seat_capacity)
SELECT train_id, 'C2', 10 FROM trains WHERE train_number='12003';

INSERT IGNORE INTO seats (coach_id, seat_number)
SELECT c.coach_id, nums.n
FROM coaches c
CROSS JOIN (
    SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
    SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL
    SELECT 9 UNION ALL SELECT 10
) nums
WHERE nums.n <= c.seat_capacity;

INSERT INTO schedules
    (train_id, route_id, departure_time, arrival_time, fare, status)
SELECT t.train_id, r.route_id,
       TIMESTAMP(CURRENT_DATE + INTERVAL 6 DAY, '08:00:00'),
       TIMESTAMP(CURRENT_DATE + INTERVAL 6 DAY, '12:00:00'),
       150.00, 'SCHEDULED'
FROM trains t JOIN routes r
WHERE t.train_number='12001'
  AND r.route_name='Thiruvananthapuram - Ernakulam'
  AND NOT EXISTS (SELECT 1 FROM schedules s WHERE s.train_id=t.train_id AND s.route_id=r.route_id AND DATE(s.departure_time)=CURRENT_DATE+INTERVAL 6 DAY);

INSERT INTO schedules
    (train_id, route_id, departure_time, arrival_time, fare, status)
SELECT t.train_id, r.route_id,
       TIMESTAMP(CURRENT_DATE + INTERVAL 7 DAY, '09:00:00'),
       TIMESTAMP(CURRENT_DATE + INTERVAL 7 DAY, '11:30:00'),
       100.00, 'SCHEDULED'
FROM trains t JOIN routes r
WHERE t.train_number='12002'
  AND r.route_name='Kollam - Kottayam'
  AND NOT EXISTS (SELECT 1 FROM schedules s WHERE s.train_id=t.train_id AND s.route_id=r.route_id AND DATE(s.departure_time)=CURRENT_DATE+INTERVAL 7 DAY);

INSERT INTO schedules
    (train_id, route_id, departure_time, arrival_time, fare, status)
SELECT t.train_id, r.route_id,
       TIMESTAMP(CURRENT_DATE + INTERVAL 8 DAY, '06:30:00'),
       TIMESTAMP(CURRENT_DATE + INTERVAL 8 DAY, '14:15:00'),
       220.00, 'SCHEDULED'
FROM trains t JOIN routes r
WHERE t.train_number='12003'
  AND r.route_name='Kottayam - Ernakulam'
  AND NOT EXISTS (SELECT 1 FROM schedules s WHERE s.train_id=t.train_id AND s.route_id=r.route_id AND DATE(s.departure_time)=CURRENT_DATE+INTERVAL 8 DAY);

INSERT INTO schedule_seats (schedule_id, seat_id, status)
SELECT sc.schedule_id, s.seat_id, 'AVAILABLE'
FROM schedules sc
JOIN trains tr ON sc.train_id=tr.train_id
JOIN coaches c ON c.train_id=tr.train_id
JOIN seats s ON s.coach_id=c.coach_id
LEFT JOIN schedule_seats ss ON ss.schedule_id=sc.schedule_id AND ss.seat_id=s.seat_id
WHERE ss.schedule_seat_id IS NULL;

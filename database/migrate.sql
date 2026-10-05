USE railway_ticket_system;

SET @fare_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'schedules'
      AND column_name = 'fare'
);

SET @sql = IF(
    @fare_column_exists = 0,
    'ALTER TABLE schedules ADD COLUMN fare DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER arrival_time',
    'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE schedules
SET fare = 100.00
WHERE fare IS NULL OR fare = 0.00;


/*
 * Allow a schedule seat to be booked again after
 * the previous booking was cancelled.
 *
 * Historical booking_seats records are kept.
 */
SET @unique_index_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'booking_seats'
      AND index_name = 'schedule_seat_id'
      AND non_unique = 0
);

SET @sql = IF(
    @unique_index_exists > 0,
    'ALTER TABLE booking_seats DROP INDEX schedule_seat_id',
    'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
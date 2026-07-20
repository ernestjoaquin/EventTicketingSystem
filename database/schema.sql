-- ============================================================
-- Event Ticketing System - Database Schema
-- For use with XAMPP (MySQL/MariaDB via phpMyAdmin)
-- ============================================================

CREATE DATABASE IF NOT EXISTS event_ticketing_system;
USE event_ticketing_system;

-- ------------------------------------------------------------
-- USERS  (maps to abstract User -> Customer / Admin in class diagram)
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    phone       VARCHAR(20),
    password    VARCHAR(255)  NOT NULL,          -- stored as SHA-256 hash
    role        ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- VENUES
-- ------------------------------------------------------------
CREATE TABLE venues (
    venue_id    INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    address     VARCHAR(255),
    capacity    INT DEFAULT 0
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- CATEGORIES
-- ------------------------------------------------------------
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- EVENTS
-- ------------------------------------------------------------
CREATE TABLE events (
    event_id        INT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    event_datetime  DATETIME NOT NULL,
    venue_id        INT NOT NULL,
    category_id     INT NOT NULL,
    total_seats     INT NOT NULL DEFAULT 0,
    available_seats INT NOT NULL DEFAULT 0,
    image_url       VARCHAR(255),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- SEATS  (per event)
-- ------------------------------------------------------------
CREATE TABLE seats (
    seat_id      INT AUTO_INCREMENT PRIMARY KEY,
    event_id     INT NOT NULL,
    section      VARCHAR(50)  NOT NULL DEFAULT 'General',
    seat_number  VARCHAR(20)  NOT NULL,
    price        DECIMAL(10,2) NOT NULL DEFAULT 0,
    status       ENUM('AVAILABLE','RESERVED','SOLD') NOT NULL DEFAULT 'AVAILABLE',
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- BOOKINGS
-- ------------------------------------------------------------
CREATE TABLE bookings (
    booking_id    INT AUTO_INCREMENT PRIMARY KEY,
    customer_id   INT NOT NULL,
    booking_date  DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(10,2) NOT NULL DEFAULT 0,
    status        ENUM('PENDING','CONFIRMED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- BOOKING ITEMS  (one row per seat/ticket in a booking)
-- ------------------------------------------------------------
CREATE TABLE booking_items (
    item_id     INT AUTO_INCREMENT PRIMARY KEY,
    booking_id  INT NOT NULL,
    event_id    INT NOT NULL,
    seat_id     INT NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    status      VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(seat_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- PAYMENTS
-- ------------------------------------------------------------
CREATE TABLE payments (
    payment_id      INT AUTO_INCREMENT PRIMARY KEY,
    booking_id      INT NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    method          VARCHAR(50) DEFAULT 'Credit Card',
    status          ENUM('PENDING','SUCCESS','FAILED') NOT NULL DEFAULT 'PENDING',
    paid_at         DATETIME NULL,
    transaction_id  VARCHAR(100),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- SEED DATA
-- ============================================================

-- Default admin account -> password: admin123
INSERT INTO users (name, email, phone, password, role) VALUES
('System Admin', 'admin@ticketing.com', '09171234567',
 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'ADMIN');
-- (password hash generated with SHA-256 of "admin123", see PasswordUtil.java)

INSERT INTO categories (name, description) VALUES
('Concert', 'Live music concerts and performances'),
('Conference', 'Business and tech conferences'),
('Sports', 'Sporting events and matches'),
('Theater', 'Theatrical plays and musicals');

INSERT INTO venues (name, address, capacity) VALUES
('SM Mall of Asia Arena', 'Pasay City, Metro Manila', 20000),
('Araneta Coliseum', 'Cubao, Quezon City', 16000),
('Bacolod Convention Center', 'Bacolod City, Negros Occidental', 3000);

INSERT INTO events (title, description, event_datetime, venue_id, category_id, total_seats, available_seats, image_url) VALUES
('Summer Music Fest', 'A night of live bands and top artists.', '2026-08-15 19:00:00', 1, 1, 100, 100, NULL),
('Tech Innovators Conference', 'Talks from leading tech innovators.', '2026-09-05 09:00:00', 3, 2, 60, 60, NULL),
('Championship Basketball Finals', 'The biggest basketball showdown of the year.', '2026-08-28 18:30:00', 2, 3, 80, 80, NULL);

-- Auto-generate seats for each seeded event (General section, uniform price)
INSERT INTO seats (event_id, section, seat_number, price, status)
SELECT e.event_id, 'General', CONCAT('S-', LPAD(seq.n, 3, '0')), 
       CASE e.category_id WHEN 1 THEN 1500.00 WHEN 2 THEN 2500.00 ELSE 800.00 END,
       'AVAILABLE'
FROM events e
JOIN (
    SELECT a.N + b.N * 10 + 1 n
    FROM (SELECT 0 N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
          UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a
    CROSS JOIN (SELECT 0 N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
                UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b
) seq
WHERE seq.n <= e.total_seats;

-- =========================================================
-- Lab 7 - User Management System (Java Swing + JDBC + MySQL)
-- Run this once before starting the application.
-- =========================================================

CREATE DATABASE IF NOT EXISTS lab7db;
USE lab7db;

CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    first_name  VARCHAR(50)  NOT NULL,
    last_name   VARCHAR(50)  NOT NULL,
    gender      VARCHAR(10),
    dob         DATE,
    age         INT,
    phone       VARCHAR(20),
    email       VARCHAR(100) NOT NULL,
    continent   VARCHAR(40),
    experience  TEXT,
    hobbies     TEXT,
    photo_path  VARCHAR(255)
);

-- Prevent two users sharing an email.
-- If this fails with error 1062, remove duplicates first (see below), then re-run it.
ALTER TABLE users ADD UNIQUE (email);

-- Clean up duplicate emails, keeping the lowest id:
-- DELETE u1 FROM users u1
-- JOIN users u2 ON u1.email = u2.email AND u1.id > u2.id;

-- Sample rows
INSERT INTO users (first_name, last_name, gender, dob, age, phone, email, continent, experience, hobbies)
VALUES
('John', 'Doe',    'Male',   '1994-03-12', 32, '617-555-0101', 'john.doe@gmail.com',    'North America', 'Software testing', 'Reading'),
('Jane', 'Doe',    'Female', '1998-07-25', 28, '617-555-0102', 'jane.doe@gmail.com',    'North America', 'Frontend intern',  'Painting'),
('Priya','Sharma', 'Female', '2000-01-08', 26, '857-555-0103', 'priya.sharma@gmail.com','Asia',          'Data analyst',     'Music');

-- ---------------------------------------------------------
-- Statements used by the application (DAO layer)
-- ---------------------------------------------------------

-- CREATE
-- INSERT INTO users (first_name, last_name, gender, dob, age, phone, email, continent, experience, hobbies, photo_path)
-- VALUES (?,?,?,?,?,?,?,?,?,?,?);

-- READ
-- SELECT id, first_name, last_name, age, email, continent FROM users ORDER BY id;

-- UPDATE
-- UPDATE users SET first_name=?, last_name=?, gender=?, dob=?, age=?, phone=?, email=?,
--        continent=?, experience=?, hobbies=?, photo_path=? WHERE id=?;

-- DELETE
-- DELETE FROM users WHERE id = ?;

SELECT * FROM users;

CREATE DATABASE IF NOT EXISTS campus_events;
USE campus_events;

CREATE TABLE IF NOT EXISTS events (
    id INT PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO events (id, event_name) VALUES
(1, 'Cultural Fest'),
(2, 'Tech Symposium'),
(3, 'Sports Day')
ON DUPLICATE KEY UPDATE event_name = VALUES(event_name);

CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(30) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    event_id INT NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_registration_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT uq_student_event UNIQUE (student_id, event_id)
);

CREATE INDEX idx_registrations_event_id ON registrations(event_id);

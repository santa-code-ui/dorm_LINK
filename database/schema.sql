-- ============================================================
--  dorm_LINK  •  Campus Oriented Hostel Manager
--  MySQL schema (XAMPP / phpMyAdmin compatible)
--  Engine: InnoDB  |  Charset: utf8mb4
-- ============================================================

DROP DATABASE IF EXISTS dorm_link;
CREATE DATABASE dorm_link CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dorm_link;

-- ------------------------------------------------------------
--  COURSE   (One Course -> Many Students)
-- ------------------------------------------------------------
CREATE TABLE course (
    course_id      INT AUTO_INCREMENT PRIMARY KEY,
    course_code    VARCHAR(20)  NOT NULL UNIQUE,
    course_name    VARCHAR(120) NOT NULL,
    department     VARCHAR(120) NOT NULL,
    duration_years INT          NOT NULL DEFAULT 4,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  HOSTEL  (blocks; supports block-wise booking + ratings)
-- ------------------------------------------------------------
CREATE TABLE hostel (
    hostel_id    INT AUTO_INCREMENT PRIMARY KEY,
    hostel_name  VARCHAR(120) NOT NULL,
    block        VARCHAR(20)  NOT NULL,
    gender       ENUM('MALE','FEMALE','MIXED') NOT NULL DEFAULT 'MIXED',
    description  TEXT,
    total_rooms  INT          NOT NULL DEFAULT 0,
    cover_image  VARCHAR(255),
    avg_rating   DECIMAL(2,1) NOT NULL DEFAULT 0.0,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_hostel_block (hostel_name, block)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  ROOM   (One Room -> Many Students, capacity controlled)
-- ------------------------------------------------------------
CREATE TABLE room (
    room_id     INT AUTO_INCREMENT PRIMARY KEY,
    hostel_id   INT NOT NULL,
    room_no     VARCHAR(20) NOT NULL,
    floor       INT NOT NULL DEFAULT 0,
    room_type   ENUM('SINGLE','DOUBLE','TRIPLE','QUAD') NOT NULL DEFAULT 'DOUBLE',
    capacity    INT NOT NULL DEFAULT 2,
    occupied    INT NOT NULL DEFAULT 0,
    rent_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    status      ENUM('AVAILABLE','FULL','MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    photo       VARCHAR(255),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_room_hostel FOREIGN KEY (hostel_id) REFERENCES hostel(hostel_id) ON DELETE CASCADE,
    UNIQUE KEY uq_room (hostel_id, room_no)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  ADMIN
-- ------------------------------------------------------------
CREATE TABLE admin (
    admin_id      INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(60)  NOT NULL UNIQUE,
    full_name     VARCHAR(120) NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('SUPER_ADMIN','ADMIN') NOT NULL DEFAULT 'ADMIN',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  STUDENT  (CourseID FK, RoomID FK)
-- ------------------------------------------------------------
CREATE TABLE student (
    student_id    INT AUTO_INCREMENT PRIMARY KEY,
    roll_no       VARCHAR(40)  NOT NULL UNIQUE,
    full_name     VARCHAR(120) NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    dob           DATE,
    gender        ENUM('MALE','FEMALE','OTHER'),
    address       TEXT,
    course_id     INT,
    room_id       INT,
    password_hash VARCHAR(255) NOT NULL,
    photo         VARCHAR(255),
    status        ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_course FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE SET NULL,
    CONSTRAINT fk_student_room   FOREIGN KEY (room_id)   REFERENCES room(room_id)     ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  ROOM_REQUEST  (StudentID FK, RoomID FK)
-- ------------------------------------------------------------
CREATE TABLE room_request (
    request_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id   INT NOT NULL,
    room_id      INT NOT NULL,
    status       ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    note         VARCHAR(255),
    requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    decided_at   DATETIME,
    decided_by   INT,
    CONSTRAINT fk_rr_student FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_rr_room    FOREIGN KEY (room_id)    REFERENCES room(room_id)       ON DELETE CASCADE,
    CONSTRAINT fk_rr_admin   FOREIGN KEY (decided_by) REFERENCES admin(admin_id)     ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  BOOKING  (StudentID FK, RoomID FK)
-- ------------------------------------------------------------
CREATE TABLE booking (
    booking_id  INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT NOT NULL,
    room_id     INT NOT NULL,
    session     VARCHAR(20) NOT NULL,
    status      ENUM('ACTIVE','CANCELLED','COMPLETED') NOT NULL DEFAULT 'ACTIVE',
    booked_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bk_student FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_bk_room    FOREIGN KEY (room_id)    REFERENCES room(room_id)       ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  ANNOUNCEMENT  (One Admin -> Many Announcements)
-- ------------------------------------------------------------
CREATE TABLE announcement (
    announcement_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_id        INT,
    title           VARCHAR(160) NOT NULL,
    body            TEXT NOT NULL,
    category        ENUM('HOSTEL','CAMPUS','UNIVERSITY','GENERAL') NOT NULL DEFAULT 'GENERAL',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ann_admin FOREIGN KEY (admin_id) REFERENCES admin(admin_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  COMPLAINT  (One Student -> Many Complaints)
-- ------------------------------------------------------------
CREATE TABLE complaint (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id   INT NOT NULL,
    type         ENUM('MAINTENANCE','FOOD','ELECTRICITY','WATER','OTHERS') NOT NULL,
    subject      VARCHAR(160) NOT NULL,
    description  TEXT NOT NULL,
    status       ENUM('OPEN','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved_at  DATETIME,
    CONSTRAINT fk_cmp_student FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  FEE  (One Student -> Many Fee Records)
-- ------------------------------------------------------------
CREATE TABLE fee (
    fee_id      INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT NOT NULL,
    session     VARCHAR(20) NOT NULL,
    hostel_fee  DECIMAL(10,2) NOT NULL DEFAULT 0,
    mess_fee    DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_due   DECIMAL(10,2) NOT NULL DEFAULT 0,
    amount_paid DECIMAL(10,2) NOT NULL DEFAULT 0,
    status      ENUM('UNPAID','PARTIAL','PAID') NOT NULL DEFAULT 'UNPAID',
    due_date    DATE,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fee_student FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  PAYMENT  (One Fee -> Many Payments)
-- ------------------------------------------------------------
CREATE TABLE payment (
    payment_id   INT AUTO_INCREMENT PRIMARY KEY,
    fee_id       INT NOT NULL,
    receipt_no   VARCHAR(40) NOT NULL UNIQUE,
    amount       DECIMAL(10,2) NOT NULL,
    method       ENUM('UPI','CARD','NETBANKING','CASH') NOT NULL DEFAULT 'UPI',
    txn_ref      VARCHAR(80),
    paid_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pay_fee FOREIGN KEY (fee_id) REFERENCES fee(fee_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  ENTRY_EXIT_LOG  (One Student -> Many Logs ; QR module)
-- ------------------------------------------------------------
CREATE TABLE entry_exit_log (
    log_id     INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    log_type   ENUM('ENTRY','EXIT') NOT NULL,
    gate       VARCHAR(60),
    logged_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_student FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  MESS_MENU  (weekly menu per hostel)
-- ------------------------------------------------------------
CREATE TABLE mess_menu (
    menu_id     INT AUTO_INCREMENT PRIMARY KEY,
    hostel_id   INT NOT NULL,
    day_of_week ENUM('MON','TUE','WED','THU','FRI','SAT','SUN') NOT NULL,
    breakfast   VARCHAR(255),
    lunch       VARCHAR(255),
    snacks      VARCHAR(255),
    dinner      VARCHAR(255),
    CONSTRAINT fk_menu_hostel FOREIGN KEY (hostel_id) REFERENCES hostel(hostel_id) ON DELETE CASCADE,
    UNIQUE KEY uq_menu (hostel_id, day_of_week)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  RATING  (students rate their hostel; feeds hostel.avg_rating)
-- ------------------------------------------------------------
CREATE TABLE rating (
    rating_id  INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    hostel_id  INT NOT NULL,
    stars      TINYINT NOT NULL CHECK (stars BETWEEN 1 AND 5),
    review     VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rt_student FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_rt_hostel  FOREIGN KEY (hostel_id)  REFERENCES hostel(hostel_id)  ON DELETE CASCADE,
    UNIQUE KEY uq_rating (student_id, hostel_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
--  PASSWORD_RESET_OTP  (Email OTP password reset)
-- ------------------------------------------------------------
CREATE TABLE password_reset_otp (
    otp_id     INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(120) NOT NULL,
    role       ENUM('ADMIN','STUDENT') NOT NULL,
    otp_code   VARCHAR(6) NOT NULL,
    expires_at DATETIME NOT NULL,
    used       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_otp_email (email)
) ENGINE=InnoDB;

-- ============================================================
--  TRIGGERS  (keep room occupancy + hostel rating consistent)
-- ============================================================
DELIMITER $$

-- Recalculate hostel average rating after any rating change
CREATE TRIGGER trg_rating_after_insert AFTER INSERT ON rating
FOR EACH ROW
BEGIN
    UPDATE hostel h
    SET h.avg_rating = (SELECT ROUND(AVG(stars),1) FROM rating WHERE hostel_id = NEW.hostel_id)
    WHERE h.hostel_id = NEW.hostel_id;
END$$

CREATE TRIGGER trg_rating_after_update AFTER UPDATE ON rating
FOR EACH ROW
BEGIN
    UPDATE hostel h
    SET h.avg_rating = (SELECT ROUND(AVG(stars),1) FROM rating WHERE hostel_id = NEW.hostel_id)
    WHERE h.hostel_id = NEW.hostel_id;
END$$

-- Flag a room FULL automatically when capacity is reached
-- Keep room.status in sync with occupancy. Must be BEFORE UPDATE and operate on
-- NEW.* — a trigger cannot issue an UPDATE against its own table.
CREATE TRIGGER trg_room_status BEFORE UPDATE ON room
FOR EACH ROW
BEGIN
    IF NEW.status <> 'MAINTENANCE' THEN
        IF NEW.occupied >= NEW.capacity THEN
            SET NEW.status = 'FULL';
        ELSE
            SET NEW.status = 'AVAILABLE';
        END IF;
    END IF;
END$$

DELIMITER ;

-- ============================================================
--  INDEXES for common lookups
-- ============================================================
CREATE INDEX idx_student_course ON student(course_id);
CREATE INDEX idx_room_hostel    ON room(hostel_id);
CREATE INDEX idx_complaint_stat ON complaint(status);
CREATE INDEX idx_fee_status     ON fee(status);
CREATE INDEX idx_log_student    ON entry_exit_log(student_id);

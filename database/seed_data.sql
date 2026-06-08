-- ============================================================
--  dorm_LINK  •  Sample / Seed Data
--  Run AFTER schema.sql
--
--  Demo logins (password scheme: salt:sha256(salt+password))
--    Admin   ->  username: admin    password: admin123
--    Admin   ->  username: warden   password: warden123
--    Student ->  roll_no:  USTM2024CS001 .. 005  password: student123
-- ============================================================
USE dorm_link;

-- Courses ----------------------------------------------------
INSERT INTO course (course_code, course_name, department, duration_years) VALUES
('BTECH-CSE', 'B.Tech Computer Science & Engineering', 'Engineering', 4),
('BTECH-ECE', 'B.Tech Electronics & Communication',    'Engineering', 4),
('BSC-PHY',   'B.Sc Physics',                           'Sciences',    3),
('BBA',       'Bachelor of Business Administration',    'Management',  3),
('MCA',       'Master of Computer Applications',        'Engineering', 2);

-- Hostels (blocks) -------------------------------------------
INSERT INTO hostel (hostel_name, block, gender, description, total_rooms, cover_image, avg_rating) VALUES
('Brahmaputra House', 'A', 'MALE',   'Riverside block with wide balconies and a quiet reading lounge.', 40, 'assets/hostel_images/brahmaputra.jpg', 4.3),
('Kamakhya House',    'B', 'FEMALE', 'Central block close to the mess and library, 24x7 security.',     38, 'assets/hostel_images/kamakhya.jpg',   4.6),
('Kaziranga House',   'C', 'MALE',   'Newest block with high-speed Wi-Fi and a rooftop court.',         42, 'assets/hostel_images/kaziranga.jpg',  4.1),
('Majuli House',      'D', 'FEMALE', 'Garden-facing rooms, dedicated study halls on every floor.',      36, 'assets/hostel_images/majuli.jpg',     4.4);

-- Rooms ------------------------------------------------------
INSERT INTO room (hostel_id, room_no, floor, room_type, capacity, occupied, rent_amount, status, photo) VALUES
(1, 'A-101', 1, 'DOUBLE', 2, 1, 45000, 'AVAILABLE', 'assets/hostel_images/room_double.jpg'),
(1, 'A-102', 1, 'DOUBLE', 2, 2, 45000, 'FULL',      'assets/hostel_images/room_double.jpg'),
(1, 'A-201', 2, 'TRIPLE', 3, 0, 38000, 'AVAILABLE', 'assets/hostel_images/room_triple.jpg'),
(2, 'B-101', 1, 'SINGLE', 1, 1, 60000, 'FULL',      'assets/hostel_images/room_single.jpg'),
(2, 'B-102', 1, 'DOUBLE', 2, 1, 45000, 'AVAILABLE', 'assets/hostel_images/room_double.jpg'),
(2, 'B-205', 2, 'DOUBLE', 2, 0, 45000, 'AVAILABLE', 'assets/hostel_images/room_double.jpg'),
(3, 'C-110', 1, 'TRIPLE', 3, 1, 38000, 'AVAILABLE', 'assets/hostel_images/room_triple.jpg'),
(3, 'C-303', 3, 'QUAD',   4, 0, 32000, 'AVAILABLE', 'assets/hostel_images/room_quad.jpg'),
(4, 'D-101', 1, 'DOUBLE', 2, 0, 45000, 'AVAILABLE', 'assets/hostel_images/room_double.jpg'),
(4, 'D-204', 2, 'SINGLE', 1, 0, 60000, 'AVAILABLE', 'assets/hostel_images/room_single.jpg');

-- Admins -----------------------------------------------------
INSERT INTO admin (username, full_name, email, phone, password_hash, role) VALUES
('admin',  'System Administrator', 'admin@dormlink.ustm.ac.in',  '9876500001', 'a1b2c3d4:fb5d9217701be2692e4865e1dafc13363d80a9529cb892cff59bc00586ef912e', 'SUPER_ADMIN'),
('warden', 'Hostel Warden',        'warden@dormlink.ustm.ac.in', '9876500002', 'e5f6a7b8:de9ecfa1972ebb5ede9fb35fde954a33738c4715e063544be93fc468baab9cee', 'ADMIN');

-- Students ---------------------------------------------------
INSERT INTO student (roll_no, full_name, email, phone, dob, gender, address, course_id, room_id, password_hash, status) VALUES
('USTM2024CS001', 'Samboraa Borgohain', 'sambora@stu.ustm.ac.in', '9000000001', '2003-02-14', 'MALE',   'Guwahati, Assam',  1, 1, 's001salt:c05c24b8a2e4abf556f54549514c319d543afea57e0464b0beb95f5e6e5692aa', 'ACTIVE'),
('USTM2024CS002', 'Ankita Das',         'ankita@stu.ustm.ac.in',  '9000000002', '2003-07-09', 'FEMALE', 'Shillong, Meghalaya', 1, 4, 's002salt:4428c7cf56d07cfdf2007ce9eb2aa2db689cbaec79a86063b04847a6528266aa', 'ACTIVE'),
('USTM2024EC003', 'Rahul Nath',         'rahul@stu.ustm.ac.in',   '9000000003', '2002-11-23', 'MALE',   'Dibrugarh, Assam', 2, 7, 's003salt:68e4603073de3d9fcfa7978bb38f4c24d5787f30e9efd9d4b841e47d1f727135', 'ACTIVE'),
('USTM2024BB004', 'Priya Sharma',       'priya@stu.ustm.ac.in',   '9000000004', '2004-01-30', 'FEMALE', 'Tezpur, Assam',    4, 5, 's004salt:7db61868664e11e7dd670145cf4df428119aefbe1914505f69fc9b2a8fc25afb', 'ACTIVE'),
('USTM2024CS005', 'Imran Hussain',      'imran@stu.ustm.ac.in',   '9000000005', '2003-05-18', 'MALE',   'Jorhat, Assam',    1, NULL, 's005salt:6259c748c00cfc86600841a0e798cb7b7b12c92d84741e32cc4b78027e3463c4', 'ACTIVE');

-- Bookings ---------------------------------------------------
INSERT INTO booking (student_id, room_id, session, status) VALUES
(1, 1, '2024-2025', 'ACTIVE'),
(2, 4, '2024-2025', 'ACTIVE'),
(3, 7, '2024-2025', 'ACTIVE'),
(4, 5, '2024-2025', 'ACTIVE');

-- Room requests ----------------------------------------------
INSERT INTO room_request (student_id, room_id, status, note) VALUES
(5, 3, 'PENDING',  'Prefer ground floor near study hall.'),
(5, 9, 'PENDING',  'Alternate choice if A-201 unavailable.');

-- Announcements ----------------------------------------------
INSERT INTO announcement (admin_id, title, body, category) VALUES
(1, 'Hostel Day 2025', 'Cultural night on 20th March at the central lawn. All residents welcome!', 'HOSTEL'),
(1, 'Mess Fee Window Open', 'Pay your 2024-25 mess fees before the 15th to avoid late charges.', 'UNIVERSITY'),
(2, 'Water Maintenance', 'Block B water supply paused on Sunday 8 AM - 12 PM for tank cleaning.', 'HOSTEL'),
(2, 'New Library Hours', 'Campus library now open till 11 PM during exam season.', 'CAMPUS');

-- Complaints -------------------------------------------------
INSERT INTO complaint (student_id, type, subject, description, status) VALUES
(1, 'ELECTRICITY', 'Tube light flickering', 'The tube light in A-101 flickers every evening.', 'OPEN'),
(2, 'WATER',       'Low pressure in washroom', 'Water pressure drops sharply after 9 PM.',      'IN_PROGRESS'),
(3, 'FOOD',        'Dinner served cold',       'Dinner is often cold by the time block C is served.', 'OPEN');

-- Fees -------------------------------------------------------
INSERT INTO fee (student_id, session, hostel_fee, mess_fee, total_due, amount_paid, status, due_date) VALUES
(1, '2024-2025', 45000, 36000, 81000, 81000, 'PAID',    '2024-09-15'),
(2, '2024-2025', 60000, 36000, 96000, 50000, 'PARTIAL', '2024-09-15'),
(3, '2024-2025', 38000, 36000, 74000, 0,     'UNPAID',  '2024-09-15'),
(4, '2024-2025', 45000, 36000, 81000, 0,     'UNPAID',  '2024-09-15');

-- Payments ---------------------------------------------------
INSERT INTO payment (fee_id, receipt_no, amount, method, txn_ref) VALUES
(1, 'RCPT-2024-0001', 81000, 'UPI',  'UPI24X8821'),
(2, 'RCPT-2024-0002', 50000, 'CARD', 'CARD24K1199');

-- Entry / Exit logs ------------------------------------------
INSERT INTO entry_exit_log (student_id, log_type, gate, logged_at) VALUES
(1, 'EXIT',  'Main Gate', '2025-03-01 08:15:00'),
(1, 'ENTRY', 'Main Gate', '2025-03-01 18:42:00'),
(2, 'EXIT',  'Block B Gate', '2025-03-01 09:05:00'),
(2, 'ENTRY', 'Block B Gate', '2025-03-01 17:20:00');

-- Mess menu (Brahmaputra House sample week) ------------------
INSERT INTO mess_menu (hostel_id, day_of_week, breakfast, lunch, snacks, dinner) VALUES
(1, 'MON', 'Aloo Paratha, Curd, Tea', 'Rice, Dal, Mixed Veg, Salad', 'Samosa, Tea', 'Roti, Paneer Butter Masala, Rice'),
(1, 'TUE', 'Idli, Sambar, Coffee',    'Rice, Rajma, Bhindi Fry',     'Pakora, Tea', 'Roti, Egg Curry, Rice'),
(1, 'WED', 'Poha, Banana, Tea',       'Rice, Dal, Aloo Gobi',        'Biscuits, Tea','Roti, Chicken Curry, Rice'),
(1, 'THU', 'Bread Omelette, Tea',     'Rice, Sambar, Cabbage',       'Maggi, Tea',  'Roti, Mix Dal, Rice'),
(1, 'FRI', 'Upma, Chutney, Coffee',   'Rice, Dal Fry, Fish Curry',   'Veg Roll, Tea','Roti, Aloo Matar, Rice'),
(1, 'SAT', 'Puri, Sabji, Tea',        'Veg Biryani, Raita',          'Samosa, Tea', 'Roti, Chana Masala, Rice'),
(1, 'SUN', 'Chole Bhature, Tea',      'Special Thali',               'Ice Cream',   'Fried Rice, Manchurian');

-- Ratings ----------------------------------------------------
INSERT INTO rating (student_id, hostel_id, stars, review) VALUES
(1, 1, 4, 'Great river view and clean rooms.'),
(2, 2, 5, 'Best block, super safe and close to everything.'),
(3, 3, 4, 'Fast Wi-Fi, friendly staff.');

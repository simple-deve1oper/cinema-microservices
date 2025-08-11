INSERT INTO bookings(user_id, session_id, status, created_by, created_date, updated_by, updated_date) VALUES
('3ca5d554-4102-4fa5-bc54-c355502b1fe5',6,'CANCELED','flyway',
 NOW() - INTERVAL '8 days', 'flyway', NOW() - INTERVAL '6 days'),

('14b8135e-4a62-4104-ac6a-26eefaeeef17',4,'PAID','flyway',
 NOW() - INTERVAL '5 days', 'flyway', NOW() - INTERVAL '4 days'),
('3c59a7b2-4cff-49b6-a654-3145ecdab36b',4,'CREATED','flyway',
 NOW() - INTERVAL '4 days', 'flyway', NOW() - INTERVAL '4 days'),
('3ca5d554-4102-4fa5-bc54-c355502b1fe5',4,'CREATED','flyway',
 NOW() - INTERVAL '4 days', 'flyway', NOW() - INTERVAL '4 days'),

('3ca5d554-4102-4fa5-bc54-c355502b1fe5',3,'PAID','flyway',
 NOW() - INTERVAL '4 days', 'flyway', NOW() - INTERVAL '3 days'),
('14b8135e-4a62-4104-ac6a-26eefaeeef17',3,'CANCELED','flyway',
 NOW() - INTERVAL '2 days', 'flyway', NOW() - INTERVAL '2 days'),
('3c59a7b2-4cff-49b6-a654-3145ecdab36b',3,'PAID','flyway',
 NOW() - INTERVAL '3 days', 'flyway', NOW() - INTERVAL '1 days'),

('3c59a7b2-4cff-49b6-a654-3145ecdab36b',5,'CANCELED','flyway',
 NOW() - INTERVAL '10 days', 'flyway', NOW() - INTERVAL '7 days'),

('3ca5d554-4102-4fa5-bc54-c355502b1fe5',7,'CREATED','flyway',
    NOW() - INTERVAL '10 days', 'flyway', NOW() - INTERVAL '1 days');


INSERT INTO booking_places(booking_id, place_id, created_by, updated_by) VALUES
(1, 76,'flyway','flyway'),
(1, 77,'flyway','flyway'),
(1, 78,'flyway','flyway'),
(1, 79,'flyway','flyway'),

(2, 36,'flyway','flyway'),
(2, 37,'flyway','flyway'),
(2, 38,'flyway','flyway'),

(3, 41,'flyway','flyway'),
(3, 42,'flyway','flyway'),
(3, 43,'flyway','flyway'),
(3, 44,'flyway','flyway'),
(3, 45,'flyway','flyway'),

(4, 49,'flyway','flyway'),
(4, 50,'flyway','flyway'),

(5, 7,'flyway','flyway'),
(5, 8,'flyway','flyway'),
(5, 9,'flyway','flyway'),

(6, 25,'flyway','flyway'),

(7, 20,'flyway','flyway'),
(7, 21,'flyway','flyway'),
(7, 22,'flyway','flyway'),

(8, 59,'flyway','flyway'),
(8, 60,'flyway','flyway'),

(9, 112,'flyway','flyway'),
(9, 113,'flyway','flyway');

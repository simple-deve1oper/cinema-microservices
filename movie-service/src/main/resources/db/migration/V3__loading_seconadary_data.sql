INSERT INTO movies_genres(movie_id, genre_id) VALUES
(1, 3), (1, 5), (2, 1),
(2, 8), (2, 9), (2, 12);

INSERT INTO movies_countries(movie_id, country_code, created_by, updated_by) VALUES
(1, '840', 'flyway', 'flyway'),
(2, '826', 'flyway', 'flyway'),
(2, '250', 'flyway', 'flyway'),
(2, '392', 'flyway', 'flyway'),
(2, '840', 'flyway', 'flyway');

INSERT INTO movies_participants(movie_id, participant_id, position, created_by, updated_by) VALUES
(1, 16, 'DIRECTOR', 'flyway', 'flyway'),
(1, 17, 'ACTOR', 'flyway', 'flyway'),
(1, 18, 'ACTOR', 'flyway', 'flyway'),
(1, 19, 'ACTOR', 'flyway', 'flyway'),
(2, 7, 'DIRECTOR', 'flyway', 'flyway'),
(2, 8, 'ACTOR', 'flyway', 'flyway'),
(2, 9, 'ACTOR', 'flyway', 'flyway'),
(2, 10, 'ACTOR', 'flyway', 'flyway'),
(2, 11, 'ACTOR', 'flyway', 'flyway'),
(2, 12, 'ACTOR', 'flyway', 'flyway'),
(2, 13, 'ACTOR', 'flyway', 'flyway'),
(2, 14, 'ACTOR', 'flyway', 'flyway'),
(2, 15, 'ACTOR', 'flyway', 'flyway');
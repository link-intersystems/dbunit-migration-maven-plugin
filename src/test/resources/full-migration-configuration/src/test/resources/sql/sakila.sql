INSERT INTO "actor" VALUES
                        (1, 'PENELOPE', 'GUINESS', TIMESTAMP '2006-02-15 04:34:33'),
                        (2, 'NICK', 'WAHLBERG', TIMESTAMP '2006-02-15 04:34:33');

INSERT INTO "language" VALUES
                           (1, 'English', TIMESTAMP '2006-02-15 05:02:19');

INSERT INTO "film" VALUES
                       (1, 'ACADEMY DINOSAUR', 'A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies', DATE '2006-01-01', 1, NULL, 6, 0.99, 86, 20.99, 'PG', 'Deleted Scenes,Behind the Scenes', TIMESTAMP '2006-02-15 05:03:42'),
                       (23, 'ANACONDA CONFESSIONS', 'A Lacklusture Display of a Dentist And a Dentist who must Fight a Girl in Australia', DATE '2006-01-01', 1, NULL, 3, 0.99, 92, 9.99, 'R', 'Trailers,Deleted Scenes', TIMESTAMP '2006-02-15 05:03:42');

INSERT INTO "film_actor" VALUES
                             (1, 1, TIMESTAMP '2006-02-15 05:05:03'),
                             (1, 23, TIMESTAMP '2006-02-15 05:05:03');


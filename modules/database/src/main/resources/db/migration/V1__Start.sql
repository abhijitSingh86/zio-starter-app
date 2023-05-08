CREATE TABLE IF NOT EXISTS city (
    id integer NOT NULL,
    name varchar NOT NULL,
    countrycode character(3) NOT NULL,
    district varchar NOT NULL,
    population integer NOT NULL
);


-- COPY city (id, name, countrycode, district, population) FROM stdin;
INSERT INTO city VALUES (1, 'Kabul', 'AFG', 'Kabol', 1780000);
INSERT INTO city VALUES (2, 'Qandahar', 'AFG', 'Qandahar', 237500);
INSERT INTO city VALUES (3, 'Herat', 'AFG', 'Herat', 186800);
INSERT INTO city VALUES (4, 'Mazar-e-Sharif', 'AFG', 'Balkh', 127800);
INSERT INTO city VALUES (5, 'Amsterdam', 'NLD', 'Noord-Holland', 731200);
INSERT INTO city VALUES (6, 'Rotterdam', 'NLD', 'Zuid-Holland', 593321);
INSERT INTO city VALUES (7, 'Haag', 'NLD', 'Zuid-Holland', 440900);
INSERT INTO city VALUES (8, 'Utrecht', 'NLD', 'Utrecht', 234323);
INSERT INTO city VALUES (9, 'Eindhoven', 'NLD', 'Noord-Brabant', 201843);
INSERT INTO city VALUES (10, 'Tilburg', 'NLD', 'Noord-Brabant', 193238);
INSERT INTO city VALUES (11, 'Groningen', 'NLD', 'Groningen', 172701);
INSERT INTO city VALUES (12, 'Breda', 'NLD', 'Noord-Brabant', 160398);
INSERT INTO city VALUES (13, 'Apeldoorn', 'NLD', 'Gelderland', 153491);
INSERT INTO city VALUES (14, 'Nijmegen', 'NLD', 'Gelderland', 152463);
INSERT INTO city VALUES (15, 'Enschede', 'NLD', 'Overijssel', 149544);
INSERT INTO city VALUES (16, 'Haarlem', 'NLD', 'Noord-Holland', 148772);
INSERT INTO city VALUES (17, 'Almere', 'NLD', 'Flevoland', 142465);
INSERT INTO city VALUES (18, 'Arnhem', 'NLD', 'Gelderland', 138020);
INSERT INTO city VALUES (19, 'Zaanstad', 'NLD', 'Noord-Holland', 135621);
INSERT INTO city VALUES (20, '´s-Hertogenbosch', 'NLD', 'Noord-Brabant', 129170);
INSERT INTO city VALUES (21, 'Amersfoort', 'NLD', 'Utrecht', 126270);
INSERT INTO city VALUES (22, 'Maastricht', 'NLD', 'Limburg', 122087);
INSERT INTO city VALUES (23, 'Dordrecht', 'NLD', 'Zuid-Holland', 119811);
INSERT INTO city VALUES (24, 'Leiden', 'NLD', 'Zuid-Holland', 117196);
INSERT INTO city VALUES (25, 'Haarlemmermeer', 'NLD', 'Noord-Holland', 110722);
INSERT INTO city VALUES (26, 'Zoetermeer', 'NLD', 'Zuid-Holland', 110214);
INSERT INTO city VALUES (27, 'Emmen', 'NLD', 'Drenthe', 105853);
INSERT INTO city VALUES (28, 'Zwolle', 'NLD', 'Overijssel', 105819);
INSERT INTO city VALUES (29, 'Ede', 'NLD', 'Gelderland', 101574);
INSERT INTO city VALUES (30, 'Delft', 'NLD', 'Zuid-Holland', 95268);

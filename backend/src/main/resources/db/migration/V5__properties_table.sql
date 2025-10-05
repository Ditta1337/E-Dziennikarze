CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE properties
(
    id            uuid         NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          varchar(255) NOT NULL UNIQUE,
    type          varchar(10)  NOT NULL,
    default_value varchar(255) NOT NULL,
    value         varchar(255) NULL
);

INSERT INTO properties (name, type, default_value, value)
VALUES ('schoolFullName', 'STRING', 'Akademia Górniczo-Hutnicza im. Stanisława Staszica w Krakowie', NULL),
       ('schoolDayStartTime', 'TIME', '07:00', NULL),
       ('schoolDayEndTime', 'TIME', '20:00', NULL),
       ('lessonDurationMinutes', 'INTEGER', '45', NULL),
       ('breakDurationMinutes', 'INTEGER', '15', NULL),
       ('maxLessonsPerDay', 'INTEGER', '8', NULL),
       ('allowTeacherPickPreferences', 'BOOLEAN', 'false', NULL);
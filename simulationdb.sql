DROP DATABASE IF EXISTS trafficSimulation;
CREATE DATABASE trafficSimulation;
USE trafficSimulation;

CREATE TABLE configuration (
    name VARCHAR(50) NOT NULL,
    value DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (name)
);

INSERT INTO configuration (name, value) VALUES
('simulationDuration', 1000),
('betweenIntersectionTime', 80),
('carGroupAvgSize', 4),
('avgCarArrivalInterval', 120),
('intersectionPassThroughTime', 15);

DROP USER IF EXISTS 'trafficappuser'@'localhost';
CREATE USER 'trafficappuser'@'localhost' IDENTIFIED BY '4Traffic!';
GRANT SELECT ON trafficSimulation.configuration TO 'trafficappuser'@'localhost';


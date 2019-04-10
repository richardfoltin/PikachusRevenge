CREATE DATABASE IF NOT EXISTS pikachusrevenge;

CREATE TABLE IF NOT EXISTS pikachusrevenge.player (
    id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
    name        VARCHAR(50),
    life        INT,
    x           INT,
    y           INT,
    actualLevel INT,
    maxLevel    INT,
    score       INT,
    difficulty  INT,
    updated     DATETIME
);

CREATE TABLE IF NOT EXISTS pikachusrevenge.pokemon (
    id            INT NOT NULL,
    player_id     INT NOT NULL REFERENCES player(id),
    level_id      INT NOT NULL REFERENCES level(id),
    name          VARCHAR(50),
    found         BIT,
    x             INT,
    y             INT,
    updated       DATETIME,
    PRIMARY KEY (id,player_id,level_id)
);

CREATE TABLE IF NOT EXISTS pikachusrevenge.level (
    id              INT NOT NULL,
    player_id       INT NOT NULL REFERENCES player(id),
    time            INT,
    updated         DATETIME,
    PRIMARY KEY (id,player_id)
);
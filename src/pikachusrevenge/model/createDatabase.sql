CREATE DATABASE IF NOT EXISTS pikachusrevenge;

CREATE TABLE IF NOT EXISTS pikachusrevenge.player (
    id          INT NOT NULL, 
    name        VARCHAR(50),
    life        INT,
    x           INT,
    y           INT,
    actualLevel INT,
    maxLevel    INT,
    score       INT,
    difficulty  INT,
    updated     DATETIME,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS pikachusrevenge.pokemon (
    player_id  INT NOT NULL,
    pokemon_id INT NOT NULL,
    level_id   INT NOT NULL,
    name       VARCHAR(50),
    found      BIT,
    x          INT,
    y          INT,
    updated     DATETIME,
    PRIMARY KEY(player_id,pokemon_id,level_id)
);

CREATE TABLE IF NOT EXISTS pikachusrevenge.level (
    player_id   INT NOT NULL,
    level_id    INT NOT NULL,
    time        INT,
    updated     DATETIME,
    PRIMARY KEY(player_id,level_id)
);
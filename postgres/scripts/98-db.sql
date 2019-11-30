CREATE TABLE tracks
(
    id              VARCHAR NOT NULL,
    title           VARCHAR NOT NULL,
    album           VARCHAR NOT NULL,
    artist          VARCHAR NOT NULL,
    genre           VARCHAR NOT NULL,
    source          VARCHAR NOT NULL,
    image           VARCHAR NOT NULL,
    track_number     INT     NOT NULL,
    total_track_count INT     NOT NULL,
    duration        INT     NOT NULL
);
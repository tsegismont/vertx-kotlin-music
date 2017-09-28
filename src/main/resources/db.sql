CREATE TABLE tracks
(
  track_id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  title    VARCHAR                           NOT NULL,
  album    VARCHAR                           NOT NULL,
  artist   VARCHAR                           NOT NULL,
  genre    VARCHAR                           NOT NULL,
  source   VARCHAR                           NOT NULL,
  duration INT                               NOT NULL
);
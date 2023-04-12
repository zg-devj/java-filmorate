DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS film_like;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS mpas;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS review_user;
DROP TABLE IF EXISTS reviews;

-- Создание таблиц

-- жанры фильмов
CREATE TABLE genres
(
    genre_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name CHARACTER VARYING(30)
);
-- MPA
CREATE TABLE mpas
(
    mpa_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name CHARACTER VARYING(5) NOT NULL
);
-- фильмы
CREATE TABLE films
(
    film_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_id       INTEGER REFERENCES mpas (mpa_id) NOT NULL,
    film_name    CHARACTER VARYING(200)           NOT NULL,
    description  CHARACTER VARYING(200),
    release_date DATE,
    duration     INTEGER CHECK (duration > 0)
);
-- сводная таблица фильм-жанр
CREATE TABLE film_genre
(
    film_id  BIGINT REFERENCES films (film_id),
    genre_id INTEGER REFERENCES genres (genre_id),
    PRIMARY KEY (film_id, genre_id)
);
-- пользователи
CREATE TABLE users
(
    user_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email     CHARACTER VARYING(255) UNIQUE NOT NULL,
    user_name CHARACTER VARYING(100),
    login     CHARACTER VARYING(100),
    birthday  DATE
);
-- пользователь поставил лайк фильму
CREATE TABLE film_like
(
    user_id BIGINT REFERENCES users (user_id),
    film_id BIGINT REFERENCES films (film_id),
    PRIMARY KEY (user_id, film_id)
);
-- друзья
CREATE TABLE friends
(
    user_id   BIGINT REFERENCES users (user_id),
    friend_id BIGINT REFERENCES users (user_id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE reviews
(
    review_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     CHARACTER VARYING(255) NOT NULL,
    is_positive BOOLEAN                NOT NULL,
    user_id     BIGINT REFERENCES users (user_id),
    film_id     BIGINT REFERENCES films (film_id)
);

CREATE TABLE review_user
(
    review_id BIGINT REFERENCES reviews (review_id),
    user_id   BIGINT REFERENCES users (user_id),
    like_it   SMALLINT NOT NULL CHECK (like_it = 1 OR like_it = -1),
    PRIMARY KEY (review_id, user_id)
);
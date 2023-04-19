-- Заполнение данными
-- добавляем жанры
INSERT INTO genres(genre_name)
VALUES ('Комедия');
INSERT INTO genres(genre_name)
VALUES ('Драма');
INSERT INTO genres(genre_name)
VALUES ('Мультфильм');
INSERT INTO genres(genre_name)
VALUES ('Триллер');
INSERT INTO genres(genre_name)
VALUES ('Документальный');
INSERT INTO genres(genre_name)
VALUES ('Боевик');

-- добовляем MPA
INSERT INTO mpas(mpa_name)
VALUES ('G');
INSERT INTO mpas(mpa_name)
VALUES ('PG');
INSERT INTO mpas(mpa_name)
VALUES ('PG-13');
INSERT INTO mpas(mpa_name)
VALUES ('R');
INSERT INTO mpas(mpa_name)
VALUES ('NC-17');

-- -- добавляем режисеров
-- INSERT INTO directors(director_name)
-- VALUES ('Директор 1');
-- INSERT INTO directors(director_name)
-- VALUES ('Директор 2');
--
-- -- добавляем фильмы
-- INSERT INTO films(mpa_id, film_name, description, release_date, duration)
-- VALUES (1, 'Комедия 1', 'Описание 1', '1990-11-12', 180);
--
-- INSERT INTO films(mpa_id, film_name, description, release_date, duration)
-- VALUES (4, 'Драма 2', 'Описание 2', '2002-09-11', 210);
--
-- INSERT INTO films(mpa_id, film_name, description, release_date, duration)
-- VALUES (5, 'Триллер 3', 'Описание 3', '2011-02-21', 192);
--
-- INSERT INTO films(mpa_id, film_name, description, release_date, duration)
-- VALUES (3, 'Боевик 4', 'Описание 4', '2005-12-04', 90);
--
-- INSERT INTO films(mpa_id, film_name, description, release_date, duration)
-- VALUES (1, 'Документальный 5', 'Описание 5', '2015-10-11', 190);
--
-- INSERT INTO films(mpa_id, film_name, description, release_date, duration)
-- VALUES (3, 'Топ', 'Описание 6', '2002-02-02', 200);
--
-- INSERT INTO film_genre (film_id, genre_id)
-- VALUES (1, 1);
-- INSERT INTO film_genre (film_id, genre_id)
-- VALUES (1, 3);
-- INSERT INTO film_genre (film_id, genre_id)
-- VALUES (2, 2);
-- INSERT INTO film_genre (film_id, genre_id)
-- VALUES (3, 3);
-- INSERT INTO film_genre (film_id, genre_id)
-- VALUES (4, 2);
-- INSERT INTO film_genre (film_id, genre_id)
-- VALUES (4, 6);
--
-- -- указываем режисеров для фильмов
-- INSERT INTO film_directors(director_id, film_id)
-- values (1, 1);
-- INSERT INTO film_directors(director_id, film_id)
-- values (1, 2);
-- INSERT INTO film_directors(director_id, film_id)
-- values (2, 1);
-- INSERT INTO film_directors(director_id, film_id)
-- values (2, 2);
-- INSERT INTO film_directors(director_id, film_id)
-- values (2, 3);
-- INSERT INTO film_directors(director_id, film_id)
-- values (1, 5);
--
-- -- добавляем пользователей
-- INSERT INTO users(email, user_name, login, birthday)
-- VALUES ('user1@example.com', 'user1', 'login1', '2000-01-10');
--
-- INSERT INTO users(email, user_name, login, birthday)
-- VALUES ('user2@example.com', 'user2', 'login2', '2001-01-10');
--
-- INSERT INTO users(email, user_name, login, birthday)
-- VALUES ('user3@example.com', 'user3', 'login3', '2002-01-10');
--
-- INSERT INTO users(email, user_name, login, birthday)
-- VALUES ('user4@example.com', 'user4', 'login4', '2003-01-10');
--
-- -- добавить лайки для фильма
-- INSERT INTO public.film_like(user_id, film_id)
-- VALUES (1, 1);
-- INSERT INTO public.film_like(user_id, film_id)
-- VALUES (1, 2);
-- INSERT INTO public.film_like(user_id, film_id)
-- VALUES (1, 3);
-- INSERT INTO public.film_like(user_id, film_id)
-- VALUES (2, 3);
-- INSERT INTO public.film_like(user_id, film_id)
-- VALUES (3, 2);
-- INSERT INTO public.film_like(user_id, film_id)
-- VALUES (3, 3);
-- INSERT INTO public.film_like(user_id, film_id)
-- VALUES (3, 4);
--
--
-- -- добавить друзей
-- INSERT INTO friends(user_id, friend_id)
-- VALUES (1, 2);
-- INSERT INTO friends(user_id, friend_id)
-- VALUES (1, 3);
-- INSERT INTO friends(user_id, friend_id)
-- VALUES (2, 1);
-- INSERT INTO friends(user_id, friend_id)
-- VALUES (2, 3);
--
-- -- добавить отзыв к фильму
-- INSERT INTO reviews(content, is_positive, user_id, film_id)
-- VALUES ('This film is sooo bad.', false, 1, 1);
-- INSERT INTO reviews(content, is_positive, user_id, film_id)
-- VALUES ('This film is sooo good.', true, 2, 2);
--
-- -- добавить оценку пользователя к отзыву
-- INSERT INTO review_user(review_id, user_id, like_it)
-- VALUES (1, 3, 1);
-- INSERT INTO review_user(review_id, user_id, like_it)
-- VALUES (1, 4, 1);
--
-- INSERT INTO review_user(review_id, user_id, like_it)
-- VALUES (2, 3, -1);
-- INSERT INTO review_user(review_id, user_id, like_it)
-- VALUES (2, 4, -1);
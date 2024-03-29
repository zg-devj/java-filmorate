package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAllFilms();

    List<Film> findPopularFilms(Integer genreId, Integer year, int limit);

    Optional<Film> findFilmById(Long filmId);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Boolean checkFilm(Long filmId);

    Collection<Film> getRecommendations(Long userId);

    List<Film> commonUserMovies(Long userId, Long friendId);

    Collection<Film> getAllFilmsSorted(Integer directorId, String sortBy);

    void deleteLikesByFilmId(Long id);

    void deleteFilm(Long id);

    public List<Film> searchForMoviesByDirector(String query);

    public List<Film> searchForMoviesByTitle(String query);
}

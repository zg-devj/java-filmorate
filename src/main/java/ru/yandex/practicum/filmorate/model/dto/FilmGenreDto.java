package ru.yandex.practicum.filmorate.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FilmGenreDto {
    private Long filmId;
    private Integer genreId;
    private String genreName;
}

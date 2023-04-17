package ru.yandex.practicum.filmorate.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FilmDirectorDto {
    private Long filmId;
    private Integer directorId;
    private String directorName;
}

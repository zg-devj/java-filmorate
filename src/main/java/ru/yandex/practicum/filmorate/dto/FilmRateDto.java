package ru.yandex.practicum.filmorate.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.model.Film;

@Setter
@Getter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FilmRateDto extends Film {
    private Integer rate;
}

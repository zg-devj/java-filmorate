package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private Long reviewId;
    @NotBlank(message = "Отзыв не может быть пустым.")
    @Size(max = 255, message = "Длина отзыва не должна быть больше 255 символов.")
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private Long useful = 0L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(reviewId, review.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId);
    }
}

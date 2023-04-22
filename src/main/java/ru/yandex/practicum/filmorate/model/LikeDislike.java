package ru.yandex.practicum.filmorate.model;

public enum LikeDislike {
    LIKE(1), DISLIKE(-1);

    private final int value;

    LikeDislike(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

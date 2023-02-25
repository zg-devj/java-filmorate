package ru.yandex.practicum.filmorate.utils;

/**
 * Получение идентификатора
 */
public class Identifier {
    private long id;

    public Identifier() {
        this.id = 0;
    }

    /**
     * увеличить и вернуть id
     * @return int идентификатор
     */
    public long next() {
        return ++id;
    }

    /**
     * Устанавливаем начальное значение идентификатора
     * @param newId начальное значение
     */
    public void setStartId(long newId) {
        if (this.id < newId) {
            this.id = newId;
        }
    }
}

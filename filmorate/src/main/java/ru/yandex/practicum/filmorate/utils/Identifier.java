package ru.yandex.practicum.filmorate.utils;

/**
 * Получение идентификатора
 */
public class Identifier {
    private int id;

    public Identifier() {
        this.id = 0;
    }

    /**
     * увеличить и вернуть id
     * @return int идентификатор
     */
    public int next() {
        return ++id;
    }

    /**
     * Устанавливаем начальное значение идентификатора
     * @param newId начальное значение
     */
    public void setStartId(int newId) {
        if (this.id < newId) {
            this.id = newId;
        }
    }
}

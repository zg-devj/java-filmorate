package ru.yandex.practicum.filmorate.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdentifierTest {
    @Test
    public void next_ReturnAddedIs1() {
        Identifier identifier = new Identifier();
        long nextId = identifier.next();
        assertEquals(1, nextId, "Сгенерированный ID должен быть 1");
    }

    @Test
    public void setMaxId_ReturnRestoredId4_IfSetMaxIsId3() {
        Identifier identifier = new Identifier();
        identifier.setStartId(3);
        long nextId = identifier.next();
        assertEquals(4, nextId, "Сгенерированный ID должен быть 4");
    }

}
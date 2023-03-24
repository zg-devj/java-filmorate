package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAllMpas() {
        Collection<Mpa> allMpas = mpaStorage.findAllMpas();
        log.debug("Запрошены все mpa рейтинги в количестве {}.", allMpas.size());
        return allMpas;
    }

    public Mpa findMpaById(Integer mpaId) {
        ValidateUtil.validNumberNotNull(mpaId, "id mpa рейтинга не должно быть null.");
        Mpa mpa = mpaStorage.findMpaById(mpaId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("MPA с %d не найден.", mpaId));
                    return null;
                }
        );
        log.debug("Запрошен рейтинг c id={}.", mpaId);
        return mpa;
    }
}

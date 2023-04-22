package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Collection<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(Integer id) {
        Director director = directorStorage.getDirectorById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Режиссера с id %d нет в базе", id));
                    return null;
                }
        );
        log.info("Запрошен фильм c id={}.", id);
        return director;
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        if (!directorStorage.isDirectorExists(director.getId())) {
            throw new NotFoundException(String.format("Режиссера с id %d нет в базе", director.getId()));
        }
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(Integer directorId) {
        if (!directorStorage.isDirectorExists(directorId)) {
            throw new NotFoundException(String.format("Режиссера с id %d нет в базе", directorId));
        }
        directorStorage.deleteDirector(directorId);
    }
}

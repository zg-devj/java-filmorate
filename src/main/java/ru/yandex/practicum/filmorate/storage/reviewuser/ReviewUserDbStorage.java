package ru.yandex.practicum.filmorate.storage.reviewuser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewUserDbStorage implements ReviewUserStorage {
    @Override
    public void createLike(Long reviewId, Long userId) {

    }

    @Override
    public void createDislike(Long reviewId, Long userId) {

    }

    @Override
    public void delete(Long reviewId, Long userId) {

    }

    @Override
    public void deleteAllByUserId(Long userId) {

    }

    @Override
    public void deleteAllByReviewId(Long reviewId) {

    }
}

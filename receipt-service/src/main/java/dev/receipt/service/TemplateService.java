package dev.receipt.service;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.user.dto.UserResponse;

/**
 * Интерфейс для создания данных для последующей генерации в квитанцию
 */
public interface TemplateService {
    /**
     * Создание данных
     * @param bookingResponse - объект типа {@link BookingResponse}
     * @param movieResponse - объект типа {@link MovieResponse}
     * @param userResponse - объект типа {@link UserResponse}
     */
    String createContent(BookingResponse bookingResponse, MovieResponse movieResponse, UserResponse userResponse);
}

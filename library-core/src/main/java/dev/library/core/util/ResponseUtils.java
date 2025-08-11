package dev.library.core.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Вспомогательный класс для построения ответов
 */
public class ResponseUtils {
    /**
     * Построение строки для 201 ответа
     * @param id - идентификатор
     */
    public static String createEntityLocation(Object id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUriString();
    }
}

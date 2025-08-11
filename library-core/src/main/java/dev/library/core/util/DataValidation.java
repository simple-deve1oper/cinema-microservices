package dev.library.core.util;

import dev.library.core.exception.BadRequestException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Вспомогательный класс для проверки входных данных
 */
public class DataValidation {
    /**
     * Проверка на ошибки
     * @param bindingResult - объект типа {@link BindingResult}
     */
    public static void checkValidation(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = getErrorsFromValidation(bindingResult);

            throw new BadRequestException("Ошибка валидации", errors);
        }
    }

    /**
     * Получение ошибок
     * @param bindingResult - объект типа {@link BindingResult}
     */
    private static Map<String, String> getErrorsFromValidation(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String, String> errors = new HashMap<>();
        fieldErrors.forEach(
                fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        return errors;
    }
}

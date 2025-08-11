package dev.library.core.util;

import dev.library.core.exception.ServerException;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * Вспомогательный класс для работы с данными с помощью Reflection API
 */
public class ReflectionUtils {
    /**
     * Получение имени поля
     */
    public static Optional<String> getFieldName(Object obj, Object value) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(obj);
                if (Objects.nonNull(fieldValue) && fieldValue.equals(value)) {
                    return Optional.of(field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new ServerException(e.getMessage());
            }
        }

        return Optional.empty();
    }

    /**
     * Проверка на то, что все поля пустые
     */
    public static boolean allFieldsIsNull(Object obj) {
        if (obj == null) {
            return true;
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(obj) != null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new ServerException(e.getMessage());
            }
        }

        return true;
    }
}

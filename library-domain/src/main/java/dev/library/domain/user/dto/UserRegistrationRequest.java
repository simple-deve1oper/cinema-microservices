package dev.library.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO для создания данных по пользователю
 * @param username - username
 * @param password - пароль
 * @param email - электронная почта
 * @param firstName - имя
 * @param lastName - фамилия
 * @param birthDate - дата рождения
 */
@Schema(
        name = "UserRegistrationRequest",
        description = "DTO для создания данных по пользователю"
)
public record UserRegistrationRequest(
        @Schema(name = "username", description = "Username")
        @NotBlank(message = "Username не может быть пустым")
        @Size(max = 255, message = "Username не может содержать более 255 символов")
        String username,
        @Schema(name = "password", description = "Пароль")
        @NotNull(message = "Пароль не может быть пустым")
        @Size(min = 10, message = "Пароль должен содержать не менее 10 символов")
        String password,
        @Schema(name = "email", description = "Электронная почта")
        @NotNull(message = "Электронная почта не может быть пустой")
        @Pattern(regexp = "[a-zA-z0-9]+@[a-z]+\\.[a-z]+", message = "Электронная почта должна быть записана в формате 'test@example.com'")
        String email,
        @Schema(name = "firstName", description = "Имя")
        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 255, message = "Имя не может содержать более 255 символов")
        String firstName,
        @Schema(name = "lastName", description = "Фамилия")
        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(max = 255, message = "Фамилия не может содержать более 255 символов")
        String lastName,
        @Schema(name = "birthDate", description = "Дата рождения")
        @NotNull(message = "Дата рождения не может быть пустой")
        LocalDate birthDate
) {}
package dev.library.domain.file.dto.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для описания типов файла
 */
@Schema(
        name = "FileType",
        description = "Перечисление для описания типов файла"
)
public enum FileType {
    IMAGE
}

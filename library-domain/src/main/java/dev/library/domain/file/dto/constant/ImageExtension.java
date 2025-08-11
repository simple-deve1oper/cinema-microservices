package dev.library.domain.file.dto.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для описания расширений изображений
 */
@Schema(
        name = "ImageExtension",
        description = "Перечисление для описания расширений изображений"
)
public enum ImageExtension {
    JPG("jpg"), JPEG("jpeg"), PNG("png");

    private final String value;

    ImageExtension(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

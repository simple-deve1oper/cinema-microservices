-- Изображения для фильмов
CREATE TABLE IF NOT EXISTS images (
    -- Идентификатор
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    -- Идентификатор фильма
    movie_id BIGINT NOT NULL,
    -- Наименование изображения
    file_name VARCHAR(255) UNIQUE NOT NULL,
    -- Порядковый номер изображения для отображения
    number INT NOT NULL,
    -- Кто создал запись
    created_by VARCHAR(100),
    -- Дата и время создания записи
    created_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Кто обновил запись
    updated_by VARCHAR(100),
    -- Дата и время обновления записи
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
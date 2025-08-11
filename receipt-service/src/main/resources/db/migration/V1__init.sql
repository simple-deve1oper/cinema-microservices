CREATE TABLE IF NOT EXISTS receipts (
    -- Идентификатор
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    -- Идентификатор фильма
    booking_id BIGINT UNIQUE NOT NULL,
    -- Идентификатор пользователя
    user_id VARCHAR(36) NOT NULL,
    -- Файл квитанции
    data BYTEA NOT NULL,
    -- Кто создал запись
    created_by VARCHAR(100),
    -- Дата и время создания записи
    created_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Кто обновил запись
    updated_by VARCHAR(100),
    -- Дата и время обновления записи
    updated_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
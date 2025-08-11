package dev.notification.service;

import org.springframework.core.io.InputStreamSource;

/**
 * Интерфейс для описания абстрактных методов по работе с электронной почтой
 */
public interface MailSendingService {
    /**
     * Отправка сообщений без файла
     * @param to - получатель
     * @param subject - тема
     * @param body - сообщение
     */
    void sendMessage(String to, String subject, String body);

    /**
     * Отправка сообщения с файлом
     * @param to - получатель
     * @param subject - тема
     * @param body - сообщение
     * @param attachmentFilename - наименование вложения
     * @param inputStreamSource - вложение
     */
    void sendMessage(String to, String subject, String body, String attachmentFilename, InputStreamSource inputStreamSource);
}

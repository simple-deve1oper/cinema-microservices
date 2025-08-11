package dev.receipt.service;

/**
 * Интерфейс для описания абстрактных методов по генерации квитанций
 */
public interface GenerateDocumentService {
    /**
     * Генерация квитанции в PDF файл
     * @param content - строка с HTML данными
     */
    byte[] generateReceipt(String content);
}

package dev.receipt.service.impl;

import com.lowagie.text.pdf.BaseFont;
import dev.library.core.exception.ServerException;
import dev.receipt.service.GenerateDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;

/**
 * Сервис, реализующий интерфейс {@link GenerateDocumentService}
 */
@Service
@Slf4j
public class GenerateDocumentServiceImpl implements GenerateDocumentService {
    @Value("${font.path}")
    private String font;

    @Override
    public byte[] generateReceipt(String content) {
        log.debug("Started generateReceipt(String content) with content = {}", content);
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont(font, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            renderer.setDocumentFromString(content);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new ServerException("Ошибка генерации квитанции");
        }
    }
}

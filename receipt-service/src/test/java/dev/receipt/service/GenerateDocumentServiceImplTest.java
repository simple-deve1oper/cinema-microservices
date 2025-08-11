package dev.receipt.service;

import dev.receipt.service.impl.GenerateDocumentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class GenerateDocumentServiceImplTest {
    final GenerateDocumentService service = new GenerateDocumentServiceImpl();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "font", "DejaVuSerif-Bold.ttf");
    }

    @Test
    void generateReceipt() {
        String content = "<html><p>Content</p></html>";
        byte[] data = service.generateReceipt(content);
        Assertions.assertNotNull(data);
    }
}

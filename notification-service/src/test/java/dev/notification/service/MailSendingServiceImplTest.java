package dev.notification.service;

import dev.notification.service.impl.MailSendingServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class MailSendingServiceImplTest {
    final JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
    final MailSendingService service = new MailSendingServiceImpl(mailSender);

    @Test
    void sendMessage_one() {
        Mockito
                .doNothing()
                .when(mailSender)
                .send(Mockito.any(SimpleMailMessage.class));

        service.sendMessage("abc1234@mail.com", "Test", "Test");

        Mockito
                .verify(mailSender, Mockito.times(1))
                .send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    void sendMessage_two() {
        Mockito
                .when(mailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));
        Mockito
                .doNothing()
                .when(mailSender)
                .send(Mockito.any(SimpleMailMessage.class));

        service.sendMessage("abc1234@mail.com", "Test", "Test", "file",
                Mockito.mock(InputStreamSource.class));

        Mockito
                .verify(mailSender, Mockito.times(1))
                .createMimeMessage();
        Mockito
                .verify(mailSender, Mockito.times(1))
                .send(Mockito.any(MimeMessage.class));
    }
}

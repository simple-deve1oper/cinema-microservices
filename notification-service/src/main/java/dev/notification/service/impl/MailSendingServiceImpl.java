package dev.notification.service.impl;

import dev.library.core.exception.ServerException;
import dev.notification.service.MailSendingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Сервис, реализующий интерфейс {@link MailSendingService}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailSendingServiceImpl implements MailSendingService {
    private final JavaMailSender mailSender;

    @Override
    public void sendMessage(String to, String subject, String body) {
        log.debug("Started sendMessage(String to, String subject, String body) with to = {}, subject = {}, body = {}",
                to, subject, body);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("no-reply@cinema.com");

        mailSender.send(message);
    }

    @Override
    public void sendMessage(String to, String subject, String body, String attachmentFilename, InputStreamSource inputStreamSource) {
        log.debug("Started sendMessage(String to, String subject, String body, String attachmentFilename, InputStreamSource inputStreamSource) with to = {}, subject = {}, body = {}, attachmentFilename = {}, inputStreamSource = {}",
                to, subject, body, attachmentFilename, inputStreamSource);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(body);
            messageHelper.setFrom("no-reply@cinema.com");
            messageHelper.addAttachment(attachmentFilename, inputStreamSource, MediaType.APPLICATION_PDF_VALUE);
        } catch (MessagingException ex) {
            throw new ServerException("Ошибка генерации почтового сообщения");
        }

        mailSender.send(message);
    }
}

package com.example.labwork3.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String        fromAddress;

    @Autowired
    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender   = mailSender;
        this.fromAddress  = fromAddress;
    }

    /**
     * Отправка простого текстового письма
     */
    public void sendSimpleEmail(List<String> to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    /**
     * Отправка HTML-письма
     */
    public void sendHtmlEmail(List<String> to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_NO, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException ex) {
            throw new RuntimeException("Failed to send HTML email", ex);
        }
    }

    /**
     * Отправка письма с вложением
     */
    public void sendEmailWithAttachment(List<String> to,
                                        String subject,
                                        String body,
                                        String attachmentPath,
                                        String attachmentName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);

            helper.setFrom(fromAddress);
            helper.setTo(to.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(body, true);

            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(
                    (attachmentName != null ? attachmentName : file.getFilename()),
                    file
            );

            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException ex) {
            throw new RuntimeException("Failed to send email with attachment", ex);
        }
    }
}

// src/main/java/com/example/labwork3/controller/EmailController.java
package com.example.labwork3.controller;

import com.example.labwork3.dto.EmailRequest;
import com.example.labwork3.dto.MassEmailRequest;
import com.example.labwork3.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-simple-email")
    public ResponseEntity<?> sendSimpleEmail(@RequestBody EmailRequest r) {
        emailService.sendSimpleEmail(r.getTo(), r.getSubject(), r.getBody());
        return ResponseEntity.ok("Simple email sent successfully!");
    }

    @PostMapping("/send-html-email")
    public ResponseEntity<?> sendHtmlEmail(@RequestBody EmailRequest r) {
        emailService.sendHtmlEmail(r.getTo(), r.getSubject(), r.getBody());
        return ResponseEntity.ok("HTML email sent successfully!");
    }

    @PostMapping("/send-email-with-attachment")
    public ResponseEntity<?> sendEmailWithAttachment(@RequestBody EmailRequest r) {
        if (r.getAttachmentPath() == null) {
            return ResponseEntity.badRequest().body("Attachment path is required");
        }
        emailService.sendEmailWithAttachment(
                r.getTo(), r.getSubject(), r.getBody(),
                r.getAttachmentPath(), r.getAttachmentName()
        );
        return ResponseEntity.ok("Email with attachment sent successfully!");
    }

    // ===== НОВО: массовая рассылка =====
    @PostMapping("/emails/send-to-students")
    public ResponseEntity<?> sendToStudents(@RequestBody MassEmailRequest r) {
        emailService.sendSimpleEmail(r.getEmails(), r.getSubject(), r.getBody());
        return ResponseEntity.ok("Mass email sent successfully to " + r.getEmails().size() + " recipients");
    }
}

package com.example.labwork3.dto;

import java.util.List;

public class EmailRequest {
    // Можно передавать несколько получателей, либо один, разделённый запятыми.
    // Здесь используем список email-адресов.
    private List<String> to;
    private String subject;
    private String body;
    // Для отправки письма с вложением – можно указать путь к файлу и (опционально) имя файла
    private String attachmentPath;
    private String attachmentName;

    public EmailRequest() {
    }

    public EmailRequest(List<String> to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public EmailRequest(List<String> to, String subject, String body, String attachmentPath, String attachmentName) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.attachmentPath = attachmentPath;
        this.attachmentName = attachmentName;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }
}

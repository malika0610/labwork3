// src/main/java/com/example/labwork3/dto/MassEmailRequest.java
package com.example.labwork3.dto;

import java.util.List;

public class MassEmailRequest {
    private List<String> emails;
    private String       subject;
    private String       body;

    public MassEmailRequest() {}
    public List<String> getEmails()   { return emails; }
    public void setEmails(List<String> emails) { this.emails = emails; }
    public String getSubject()        { return subject; }
    public void setSubject(String subject)     { this.subject = subject; }
    public String getBody()           { return body; }
    public void setBody(String body)           { this.body = body; }
}

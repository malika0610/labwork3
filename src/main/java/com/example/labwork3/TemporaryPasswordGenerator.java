package com.example.labwork3;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TemporaryPasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String userRawPassword = "password";
        String adminRawPassword = "admin";

        String userEncoded = encoder.encode(userRawPassword);
        String adminEncoded = encoder.encode(adminRawPassword);

        System.out.println("User password (raw: " + userRawPassword + "): " + userEncoded);
        System.out.println("Admin password (raw: " + adminRawPassword + "): " + adminEncoded);
    }
}

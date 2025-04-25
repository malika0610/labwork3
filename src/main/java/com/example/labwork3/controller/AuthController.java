package com.example.labwork3.controller;

import com.example.labwork3.dto.RegistrationRequest;
import com.example.labwork3.entity.UserEntity;
import com.example.labwork3.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
        Optional<UserEntity> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        String roleToSet = (request.getRole() == null || request.getRole().isEmpty())
                ? "STUDENT" : request.getRole().toUpperCase();

        UserEntity newUser = new UserEntity();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(roleToSet);

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully with role: " + roleToSet);
    }
}

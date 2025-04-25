package com.example.labwork3.controller;

import com.example.labwork3.entity.UserEntity;
import com.example.labwork3.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final UserRepository userRepository;

    public TeacherController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Получение списка всех преподавателей (TEACHER)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserEntity> getAllTeachers() {
        return userRepository.findAll().stream()
                .filter(user -> {
                    String role = user.getRole().toUpperCase();
                    // Если роль хранится как TEACHER или с префиксом ROLE_TEACHER
                    return role.equals("TEACHER") || role.equals("ROLE_TEACHER");
                })
                .collect(Collectors.toList());
    }

    // Удаление преподавателя по id (только для ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            String role = user.getRole().toUpperCase();
            if (!role.equals("TEACHER") && !role.equals("ROLE_TEACHER")) {
                return ResponseEntity.badRequest().body("User is not a teacher");
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok("Teacher deleted successfully");
        }).orElse(ResponseEntity.badRequest().body("Teacher not found"));
    }
}

package com.example.labwork3.controller;

import com.example.labwork3.entity.Student;
import com.example.labwork3.entity.UserEntity;
import com.example.labwork3.repository.StudentRepository;
import com.example.labwork3.repository.UserRepository;
import com.example.labwork3.specifications.StudentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public StudentController(StudentRepository studentRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    // Создание студента (доступно TEACHER и ADMIN)
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    // Получение списка студентов с пагинацией, сортировкой и фильтрацией
    // Фильтры: firstName, lastName, email, а также частичное совпадение по имени через параметр name_like
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllStudents(Pageable pageable,
                                            @RequestParam(required = false) String firstName,
                                            @RequestParam(required = false) String lastName,
                                            @RequestParam(required = false) String email,
                                            @RequestParam(required = false, name = "name_like") String nameLike) {
        Specification<Student> spec = Specification.where(null);
        if (firstName != null) {
            spec = spec.and(StudentSpecification.hasFirstName(firstName));
        }
        if (lastName != null) {
            spec = spec.and(StudentSpecification.hasLastName(lastName));
        }
        if (email != null) {
            spec = spec.and(StudentSpecification.hasEmail(email));
        }
        if (nameLike != null) {
            spec = spec.and(StudentSpecification.hasNameLike(nameLike));
        }
        Page<Student> pageResult = studentRepository.findAll(spec, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        Map<String, String> filters = new HashMap<>();
        if (firstName != null) filters.put("firstName", firstName);
        if (lastName != null) filters.put("lastName", lastName);
        if (email != null) filters.put("email", email);
        if (nameLike != null) filters.put("name_like", nameLike);
        response.put("filtersApplied", filters);
        return ResponseEntity.ok(response);
    }

    // Получение студента по id: STUDENT может видеть только свою запись
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id, Principal principal) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isEmpty()) {
            return null;
        }
        Student student = studentOpt.get();
        if (hasRole("ROLE_STUDENT")) {
            UserEntity currentUser = userRepository.findByUsername(principal.getName()).orElse(null);
            if (currentUser == null || student.getUser() == null) {
                return null;
            }
            if (!student.getUser().getId().equals(currentUser.getId())) {
                return null;
            }
        }
        return student;
    }

    // Обновление студента (доступно TEACHER и ADMIN)
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        return studentRepository.findById(id).map(student -> {
            student.setFirstName(updatedStudent.getFirstName());
            student.setLastName(updatedStudent.getLastName());
            student.setDob(updatedStudent.getDob());
            student.setEmail(updatedStudent.getEmail());
            return studentRepository.save(student);
        }).orElse(null);
    }

    // Удаление студента (доступно только ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
    }

    private boolean hasRole(String roleName) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(roleName));
    }
}

// src/main/java/com/example/labwork3/controller/EnrollmentController.java
package com.example.labwork3.controller;

import com.example.labwork3.entity.Course;
import com.example.labwork3.entity.Enrollment;
import com.example.labwork3.entity.Student;
import com.example.labwork3.entity.UserEntity;
import com.example.labwork3.repository.CourseRepository;
import com.example.labwork3.repository.EnrollmentRepository;
import com.example.labwork3.repository.StudentRepository;
import com.example.labwork3.repository.UserRepository;
import com.example.labwork3.service.EmailService;
import com.example.labwork3.specifications.EnrollmentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository     studentRepository;
    private final CourseRepository      courseRepository;
    private final UserRepository        userRepository;
    private final EmailService          emailService;

    public EnrollmentController(EnrollmentRepository enrollmentRepository,
                                StudentRepository studentRepository,
                                CourseRepository courseRepository,
                                UserRepository userRepository,
                                EmailService emailService) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository    = studentRepository;
        this.courseRepository     = courseRepository;
        this.userRepository       = userRepository;
        this.emailService         = emailService;
    }

    /**
     * 1) CREATE + Авто‑рассылка письма студенту при зачислении.
     *    Доступно роли TEACHER и ADMIN.
     */
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PostMapping
    public Enrollment createEnrollment(@RequestParam Long studentId,
                                       @RequestParam Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course   = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment(student, course);
        Enrollment saved = enrollmentRepository.save(enrollment);

        // Формируем и отправляем письмо
        String to      = student.getEmail();
        String subject = "Enrollment Confirmation";
        String date    = saved.getEnrollmentDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String body = String.format(
                "Dear %s %s,%n%n" +
                        "You have been successfully enrolled in the course \"%s\" on %s.%n%n" +
                        "Best regards,",
                student.getFirstName(), student.getLastName(),
                course.getCourseName(), date
        );
        emailService.sendSimpleEmail(
                Collections.singletonList(to),
                subject,
                body
        );

        return saved;
    }

    /**
     * 2) READ ALL с пагинацией, сортировкой и фильтрацией.
     *    STUDENT видит только свои зачисления,
     *    TEACHER/ADMIN могут фильтровать по studentId и courseId.
     */
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
    @GetMapping
    public ResponseEntity<?> getAllEnrollments(Principal principal,
                                               Pageable pageable,
                                               @RequestParam(required = false) Long studentId,
                                               @RequestParam(required = false) Long courseId) {
        Specification<Enrollment> spec = Specification.where(null);

        // Если студент — ограничиваем по его user -> student
        if (hasRole("ROLE_STUDENT")) {
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElse(null);
            if (user == null) {
                return ResponseEntity.ok(Page.empty(pageable));
            }
            Student self = studentRepository.findByUser(user).orElse(null);
            if (self == null) {
                return ResponseEntity.ok(Page.empty(pageable));
            }
            spec = spec.and(EnrollmentSpecification.hasStudentId(self.getId()));
        } else {
            // TEACHER/ADMIN могут передать studentId
            if (studentId != null) {
                spec = spec.and(EnrollmentSpecification.hasStudentId(studentId));
            }
        }

        // Фильтрация по courseId
        if (courseId != null) {
            spec = spec.and(EnrollmentSpecification.hasCourseId(courseId));
        }

        Page<Enrollment> page = enrollmentRepository.findAll(spec, pageable);

        Map<String,Object> resp = new HashMap<>();
        resp.put("content",       page.getContent());
        resp.put("totalElements", page.getTotalElements());
        resp.put("totalPages",    page.getTotalPages());

        Map<String,Object> filters = new HashMap<>();
        if (studentId != null) filters.put("studentId", studentId);
        if (courseId  != null) filters.put("courseId",  courseId);
        resp.put("filtersApplied", filters);

        return ResponseEntity.ok(resp);
    }

    /**
     * 3) READ ONE — студент видит только своё.
     */
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
    @GetMapping("/{id}")
    public Enrollment getEnrollmentById(@PathVariable Long id, Principal principal) {
        Enrollment e = enrollmentRepository.findById(id).orElse(null);
        if (e == null) return null;

        if (hasRole("ROLE_STUDENT")) {
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElse(null);
            if (user == null) return null;
            Student self = studentRepository.findByUser(user).orElse(null);
            if (self == null) return null;
            if (!e.getStudent().getId().equals(self.getId())) {
                return null;
            }
        }
        return e;
    }

    /**
     * 4) UPDATE — только TEACHER и ADMIN.
     */
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PutMapping("/{id}")
    public Enrollment updateEnrollment(@PathVariable Long id,
                                       @RequestParam Long courseId) {
        return enrollmentRepository.findById(id)
                .map(e -> {
                    courseRepository.findById(courseId)
                            .ifPresent(e::setCourse);
                    return enrollmentRepository.save(e);
                })
                .orElse(null);
    }

    /**
     * 5) DELETE — только ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteEnrollment(@PathVariable Long id) {
        enrollmentRepository.deleteById(id);
    }

    // Вспомогательный метод проверки роли
    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}

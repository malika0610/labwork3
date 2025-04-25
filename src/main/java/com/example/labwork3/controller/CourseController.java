package com.example.labwork3.controller;

import com.example.labwork3.entity.Course;
import com.example.labwork3.repository.CourseRepository;
import com.example.labwork3.specifications.CourseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Создание курса (доступно TEACHER и ADMIN)
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    // Получение списка курсов с пагинацией, сортировкой и фильтрацией
    // Фильтрация по courseName (точное совпадение) или по частичному совпадению (courseName_like)
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllCourses(Pageable pageable,
                                           @RequestParam(required = false, name = "courseName") String courseName,
                                           @RequestParam(required = false, name = "courseName_like") String courseNameLike) {
        Specification<Course> spec = Specification.where(null);
        if (courseName != null) {
            spec = spec.and(CourseSpecification.hasCourseName(courseName));
        }
        if (courseNameLike != null) {
            spec = spec.and(CourseSpecification.hasCourseNameLike(courseNameLike));
        }
        Page<Course> pageResult = courseRepository.findAll(spec, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        Map<String, String> filters = new HashMap<>();
        if (courseName != null) filters.put("courseName", courseName);
        if (courseNameLike != null) filters.put("courseName_like", courseNameLike);
        response.put("filtersApplied", filters);
        return ResponseEntity.ok(response);
    }

    // Получение курса по id (доступно STUDENT, TEACHER, ADMIN)
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    // Обновление курса (доступно TEACHER и ADMIN)
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        return courseRepository.findById(id).map(course -> {
            course.setCourseName(updatedCourse.getCourseName());
            course.setDescription(updatedCourse.getDescription());
            return courseRepository.save(course);
        }).orElse(null);
    }

    // Удаление курса (доступно только ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
    }
}

package com.example.labwork3.specifications;

import com.example.labwork3.entity.Enrollment;
import org.springframework.data.jpa.domain.Specification;

public class EnrollmentSpecification {

    public static Specification<Enrollment> hasStudentId(Long studentId) {
        return (root, query, cb) -> cb.equal(root.get("student").get("id"), studentId);
    }

    public static Specification<Enrollment> hasCourseId(Long courseId) {
        return (root, query, cb) -> cb.equal(root.get("course").get("id"), courseId);
    }
}

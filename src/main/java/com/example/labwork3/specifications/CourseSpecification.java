package com.example.labwork3.specifications;

import com.example.labwork3.entity.Course;
import org.springframework.data.jpa.domain.Specification;

public class CourseSpecification {

    public static Specification<Course> hasCourseName(String courseName) {
        return (root, query, cb) -> cb.equal(root.get("courseName"), courseName);
    }

    public static Specification<Course> hasCourseNameLike(String courseNameLike) {
        return (root, query, cb) -> cb.like(root.get("courseName"), courseNameLike + "%");
    }
}

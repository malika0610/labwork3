package com.example.labwork3.specifications;

import com.example.labwork3.entity.Student;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {

    // Фильтрация по точному совпадению firstName
    public static Specification<Student> hasFirstName(String firstName) {
        return (root, query, cb) -> cb.equal(root.get("firstName"), firstName);
    }

    // Фильтрация по точному совпадению lastName
    public static Specification<Student> hasLastName(String lastName) {
        return (root, query, cb) -> cb.equal(root.get("lastName"), lastName);
    }

    // Фильтрация по точному совпадению email
    public static Specification<Student> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }

    // Фильтрация с частичным совпадением по имени или фамилии,
    // нечувствительная к регистру. Ищет строки, начинающиеся на заданную подстроку.
    public static Specification<Student> hasNameLike(String nameLike) {
        return (root, query, cb) -> {
            String pattern = nameLike.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern)
            );
        };
    }
}

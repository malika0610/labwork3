package com.example.labwork3.repository;

import com.example.labwork3.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {
    List<Enrollment> findByStudentId(Long studentId);
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);
}

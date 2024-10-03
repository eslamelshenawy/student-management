package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.CourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {
}

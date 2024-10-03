package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.CourseRegistration;
import com.example.studentmanagement.repository.CourseRegistrationRepository;
import com.example.studentmanagement.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.Cacheable;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseRegistrationRepository registrationRepository;

    @Cacheable("courses")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public void registerCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        CourseRegistration registration = new CourseRegistration();
        registration.setCourse(course);
        registrationRepository.save(registration);
    }

    public void cancelCourse(Long registrationId) {
        registrationRepository.deleteById(registrationId);
    }
}

package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.*;
import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.service.CourseScheduleService;
import com.example.studentmanagement.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated // Enable validation for incoming requests
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseScheduleService courseScheduleService;

    /**
     * Get a list of all available courses.
     * @return a response containing the list of courses.
     */
    @GetMapping("/courses")
    public ViewCoursesResponse viewCourses() {
        List<Course> courses = courseService.getAllCourses();
        ViewCoursesResponse response = new ViewCoursesResponse();
        response.setCourses(courses);
        return response;
    }

    /**
     * Register the user for a course by course ID.
     * @param request contains the course ID for registration.
     * @return a response indicating the result of the registration.
     */
    @PostMapping("/courses/register")
    public RegisterCourseResponse registerCourse(@Valid @RequestBody RegisterCourseRequest request) {
        courseService.registerCourse(request.getCourseId()); // Register the course
        RegisterCourseResponse response = new RegisterCourseResponse();
        response.setMessage("Course registered successfully.");
        return response;
    }

    /**
     * Cancel a course registration based on registration ID.
     * @param request contains the registration ID for cancellation.
     * @return a response indicating the result of the cancellation.
     */
    @DeleteMapping("/courses/cancel")
    public CancelCourseResponse cancelCourse(@Valid @RequestBody CancelCourseRequest request) {
        courseService.cancelCourse(request.getRegistrationId()); // Cancel the course registration
        CancelCourseResponse response = new CancelCourseResponse();
        response.setMessage("Course registration canceled successfully.");
        return response;
    }

    /**
     * Get the course schedule as a PDF file.
     * @return a PDF file of the course schedule.
     */
    @GetMapping("/courses/schedule")
    public ResponseEntity<byte[]> getCourseScheduleAsPDF() {
        try {
            byte[] pdfContents = courseScheduleService.generateCourseSchedulePdf();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=courses_schedule.pdf"); // Set header for PDF display

            return new ResponseEntity<>(pdfContents, headers, HttpStatus.OK);
        } catch (IOException e) {
            // Handle the IOException and return a suitable response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}

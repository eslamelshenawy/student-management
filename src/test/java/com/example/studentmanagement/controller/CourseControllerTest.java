package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.*;
import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.service.CourseScheduleService;
import com.example.studentmanagement.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test") // Use the test profile
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CourseControllerTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    @Mock
    private CourseScheduleService courseScheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks before each test
    }

    @Test
    void testViewCourses() {
        // Arrange: Prepare mock data
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Course 1");

        when(courseService.getAllCourses()).thenReturn(Collections.singletonList(course1));

        // Act: Call the controller method
        ViewCoursesResponse response = courseController.viewCourses();

        // Assert: Verify results
        assertNotNull(response);
        assertEquals(1, response.getCourses().size());
        assertEquals("Course 1", response.getCourses().get(0).getTitle());
        verify(courseService, times(1)).getAllCourses(); // Ensure method was called once
    }

    @Test
    void testRegisterCourse() {
        // Arrange: Prepare mock request
        RegisterCourseRequest request = new RegisterCourseRequest();
        request.setCourseId(1L);

        // Act: Call the controller method
        RegisterCourseResponse response = courseController.registerCourse(request);

        // Assert: Verify the response
        assertNotNull(response);
        assertEquals("Course registered successfully.", response.getMessage());
        verify(courseService, times(1)).registerCourse(1L); // Ensure registerCourse was called with the correct ID
    }

    @Test
    void testCancelCourse() {
        // Arrange: Prepare mock request
        CancelCourseRequest request = new CancelCourseRequest();
        request.setRegistrationId(1L);

        // Act: Call the controller method
        CancelCourseResponse response = courseController.cancelCourse(request);

        // Assert: Verify the response
        assertNotNull(response);
        assertEquals("Course registration canceled successfully.", response.getMessage());
        verify(courseService, times(1)).cancelCourse(1L); // Ensure cancelCourse was called with the correct ID
    }

    @Test
    void testGetCourseScheduleAsPDF() throws IOException {
        // Arrange: Prepare a PDF byte array response
        byte[] pdfContents = new byte[]{1, 2, 3}; // Mock PDF data
        when(courseScheduleService.generateCourseSchedulePdf()).thenReturn(pdfContents);

        // Act: Call the controller method
        ResponseEntity<byte[]> response = courseController.getCourseScheduleAsPDF();

        // Assert: Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(pdfContents, response.getBody());
        assertEquals("inline; filename=courses_schedule.pdf", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        verify(courseScheduleService, times(1)).generateCourseSchedulePdf(); // Ensure the service method was called
    }

    @Test
    void testGetCourseScheduleAsPDFHandlingIOException() throws IOException {
        // Arrange: Simulate IOException when generating PDF
        when(courseScheduleService.generateCourseSchedulePdf()).thenThrow(new IOException("PDF generation failed"));

        // Act: Call the controller method
        ResponseEntity<byte[]> response = courseController.getCourseScheduleAsPDF();

        // Assert: Verify the response status
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}

package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.CourseRegistration;
import com.example.studentmanagement.repository.CourseRegistrationRepository;
import com.example.studentmanagement.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test") // Use the test profile
@AutoConfigureTestDatabase(replace= Replace.ANY)
class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseRegistrationRepository registrationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCourses() {
        // Arrange: Prepare the data needed for testing
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Course 1");

        // Act: Define the expected behavior of the repository
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1));

        // Act: Call the method to test
        List<Course> courses = courseService.getAllCourses();

        // Assert: Verify the results
        assertEquals(1, courses.size());
        assertEquals("Course 1", courses.get(0).getTitle());
        verify(courseRepository, times(1)).findAll(); // Assert that findAll was called once
    }

    @Test
    void testRegisterCourse() {
        // Arrange: Prepare the data needed for testing
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Course 1");

        // Act: Define the expected behavior of the repository
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act: Call the method to register the course
        courseService.registerCourse(courseId);

        // Assert: Verify that save was called once
        verify(registrationRepository, times(1)).save(any(CourseRegistration.class));
    }

    @Test
    void testCaching() {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Course 1");

        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1));

        // Call the method for the first time, this should hit the database
        List<Course> coursesFirstCall = courseService.getAllCourses();
        assertEquals(1, coursesFirstCall.size());

        // Call the method for the second time, this should hit the cache
        List<Course> coursesSecondCall = courseService.getAllCourses();
        assertEquals(1, coursesSecondCall.size());

        // Verify the findAll method was only called once
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testRegisterCourseNotFound() {
        // Arrange: Prepare data for testing
        Long courseId = 1L;

        // Act: Define behavior to return an empty Optional
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Assert: Ensure the appropriate exception is thrown
        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.registerCourse(courseId);
        });

        String expectedMessage = "Course not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCancelCourse() {
        // Arrange: Prepare the registration ID for cancellation
        Long registrationId = 1L;

        // Act: Call the method to cancel the course registration
        courseService.cancelCourse(registrationId);

        // Assert: Verify that deleteById was called once
        verify(registrationRepository, times(1)).deleteById(registrationId);
    }

    @Test
    void testCancelCourseNotFound() {
        // Arrange: Prepare a registration ID that does not exist
        Long registrationId = 99L;

        // Act: Call the method to cancel the course registration
        doThrow(new RuntimeException("Registration not found")).when(registrationRepository).deleteById(registrationId);

        // Assert: Ensure the appropriate exception is thrown
        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.cancelCourse(registrationId);
        });

        String expectedMessage = "Registration not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}

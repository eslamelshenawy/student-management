package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.repository.CourseRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
class CourseScheduleServiceTest {

    @InjectMocks
    private CourseScheduleService courseScheduleService;

    @Mock
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateCourseSchedulePdf() throws IOException {
        // Arrange: Prepare mock courses
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Course 1");
        course1.setDescription("Description 1");
        course1.setSchedule("Schedule 1");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Course 2");
        course2.setDescription("Description 2");
        course2.setSchedule("Schedule 2");

        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1, course2));

        // Act: Generate the PDF
        byte[] pdfContents = courseScheduleService.generateCourseSchedulePdf();

        // Assert: Verify that a PDF was created and has expected content
        assertNotNull(pdfContents);
        assertTrue(pdfContents.length > 0); // Ensure the PDF is not empty

        // Optionally, you could validate the contents of the PDF by reading it back
        try (PDDocument pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfContents))) {
            assertEquals(1, pdfDocument.getNumberOfPages()); // Check if there's one page
        }
    }

    @Test
    void testGenerateCourseSchedulePdfHandlesEmptyList() throws IOException {
        // Arrange: Prepare an empty course list
        when(courseRepository.findAll()).thenReturn(Arrays.asList()); // No courses available

        // Act: Generate the PDF
        byte[] pdfContents = courseScheduleService.generateCourseSchedulePdf();

        // Assert: Check that the PDF was created
        assertNotNull(pdfContents);
        assertTrue(pdfContents.length > 0); // It should still create the PDF even if it's empty
    }

    @Test
    void testGenerateCourseSchedulePdfThrowsIOException() throws IOException {
        // Arrange: Make the repository throw an IOException
        when(courseRepository.findAll()).thenThrow(new IOException("Database access error"));

        // Act & Assert: Ensure the IOException is propagated
        Exception exception = assertThrows(IOException.class, () -> {
            courseScheduleService.generateCourseSchedulePdf();
        });

        String expectedMessage = "Database access error";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}

package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Course;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.example.studentmanagement.repository.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class CourseScheduleService {

    @Autowired
    private CourseRepository courseRepository;

    public byte[] generateCourseSchedulePdf() throws IOException {
        List<Course> courses = courseRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Course Schedule");
                contentStream.endText();

                int y = 650;
                for (Course course : courses) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, y);
                    contentStream.showText("Course Title: " + course.getTitle());
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, y - 15);
                    contentStream.showText("Description: " + course.getDescription());
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, y - 30);
                    contentStream.showText("Schedule: " + course.getSchedule());
                    contentStream.endText();

                    y -= 50;
                }
            }

            document.save(baos);
        }
        return baos.toByteArray();
    }
}

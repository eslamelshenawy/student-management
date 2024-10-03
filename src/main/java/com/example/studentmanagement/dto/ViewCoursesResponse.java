package com.example.studentmanagement.dto;


import com.example.studentmanagement.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewCoursesResponse {
    private List<Course> courses;

}

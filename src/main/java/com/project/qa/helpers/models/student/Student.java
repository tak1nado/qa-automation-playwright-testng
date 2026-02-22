package com.project.qa.helpers.models.student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Student {
    private int id;
    private StudentData studentData;

    public void update(StudentData newStudentData) {
        if (newStudentData != null) {
            this.studentData.update(newStudentData);
        }
    }

}

package com.project.qa.helpers.models.student;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
public class StudentData {
    private String firstname;
    private String lastname;
    private String email;
    private Gender gender;
    private String phoneNumber;
    private LocalDate birthDay;
    private List<Subject> subjects;
    private List<Hobby> hobbies;
    private File picture;
    private String address;
    private City city;
    private State state;

    public String getBirthDayString() {
        return convertToZonedDateString(this.birthDay);
    }

    private String convertToZonedDateString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void update(StudentData newStudentData) {
        if (newStudentData.firstname != null) {
            this.firstname = newStudentData.firstname;
        }
        if (newStudentData.lastname != null) {
            this.lastname = newStudentData.lastname;
        }
        if (newStudentData.email != null) {
            this.email = newStudentData.email;
        }
        if (newStudentData.gender != null) {
            this.gender = newStudentData.gender;
        }
        if (newStudentData.phoneNumber != null) {
            this.phoneNumber = newStudentData.phoneNumber;
        }
        if (newStudentData.birthDay != null) {
            this.birthDay = newStudentData.birthDay;
        }
        if (newStudentData.subjects != null) {
            this.subjects = newStudentData.subjects;
        }
        if (newStudentData.hobbies != null) {
            this.hobbies = newStudentData.hobbies;
        }
        if (newStudentData.picture != null) {
            this.picture = newStudentData.picture;
        }
        if (newStudentData.address != null) {
            this.address = newStudentData.address;
        }
        if (newStudentData.city != null) {
            this.city = newStudentData.city;
        }
        if (newStudentData.state != null) {
            this.state = newStudentData.state;
        }
    }

    public StudentData getCopy() {
        return StudentData.builder()
                .firstname(this.firstname)
                .lastname(this.lastname)
                .email(this.email)
                .gender(this.gender)
                .phoneNumber(this.phoneNumber)
                .birthDay(this.birthDay)
                .subjects(this.subjects)
                .hobbies(this.hobbies)
                .picture(this.picture)
                .address(this.address)
                .city(this.city)
                .state(this.state)
                .build();
    }
}

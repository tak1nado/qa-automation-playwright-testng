package com.project.qa.helpers.managers.student;

import com.project.qa.helpers.exceptions.InstanceAlreadyPickedException;
import com.project.qa.helpers.models.bookings.Booking;
import com.project.qa.helpers.user.engine.UserSession;
import com.project.qa.helpers.utils.RandomUtils;
import com.project.qa.helpers.models.student.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Component
public class StudentsManager {
    @Autowired private RandomUtils randomUtils;
    private final ArrayList<Student> students = new ArrayList<>();
    private final InheritableThreadLocal<ArrayList<Student>> tlStudents = new InheritableThreadLocal<>();
    private final ArrayList<Student> pickedStudents = new ArrayList<>();

    public Student createInstance(Integer id, StudentData studentData) {
        Student student = pick(new Student(id, studentData));
        this.students.add(student);
        return student;
    }

    public ArrayList<Student> getAllStudents() {
        return students;
    }

    public StudentData generateRandomValidStudentData() {
        City randomCity = City.getRandom();
        return StudentData.builder()
                .firstname(randomUtils.generateRandomString())
                .lastname(randomUtils.generateRandomString())
                .email(randomUtils.generateRandomEmail())
                .gender(Gender.getRandom())
                .phoneNumber(randomUtils.generateRandomNumeric(10))
                .birthDay(randomUtils.generateRandomBirthDay())
                .subjects(Subject.getRandomUnique(2))
                .hobbies(Hobby.getRandomUnique(2))
                .picture(randomUtils.getDefaultFile())
                .address(randomUtils.generateRandomAlphanumeric(10))
                .city(randomCity)
                .state(randomCity.state)
                .build();
    }

    public StudentData generateRandomValidStudentDataWithBookingData(Booking booking) {
        City randomCity = City.getRandom();
        return StudentData.builder()
                .firstname(booking.getBookingData().getFirstname())
                .lastname(booking.getBookingData().getLastname())
                .email(randomUtils.generateRandomEmail())
                .gender(Gender.getRandom())
                .phoneNumber(randomUtils.generateRandomNumeric(10))
                .birthDay(randomUtils.generateRandomBirthDay())
                .subjects(Subject.getRandomUnique(2))
                .hobbies(Hobby.getRandomUnique(2))
                .picture(randomUtils.getDefaultFile())
                .address(randomUtils.generateRandomAlphanumeric(10))
                .city(randomCity)
                .state(randomCity.state)
                .build();
    }

    public void deleteAllCreatedStudents(UserSession adminSession) {
        log.info("Students to delete: " + getAllStudents());
//        getAllStudents().forEach(student -> deleteStudent(adminSession, student)); TODO create API
        this.students.removeAll(getAllStudents());
    }

    private void deleteStudent(UserSession adminSession, Student student) {
        try {
//            studentsControllerApi.deleteBookingById(adminSession, student.getId()); TODO create API
        } catch (UndeclaredThrowableException exception) {
            log.info("Connection refused: " + exception);
        }
    }

    public synchronized List<Student> getNotPickedStudents() {
        return getAllStudents().stream()
                .filter(student -> !pickedStudents.contains(student))
                .collect(Collectors.toList());
    }

    public synchronized Student pick(Student student) throws InstanceAlreadyPickedException {
        if (!isPicked(student)) {
            this.pickedStudents.add(student);
            if (this.tlStudents.get() == null) {
                this.tlStudents.set(new ArrayList<>());
                this.tlStudents.get().add(student);
            } else if (!tlStudents.get().contains(student)) {
                this.tlStudents.get().add(student);
            }
        }
        return student;
    }

    public boolean isPicked(Student student) {
        return this.pickedStudents.contains(student);
    }

    public synchronized void unpickThreadStudents(Student student) {
        if (this.tlStudents.get() != null) {
            this.pickedStudents.remove(student);
            this.tlStudents.get().remove(student);
        }
    }

    public synchronized void unpickThreadStudents() {
        if (this.tlStudents.get() != null) {
            this.pickedStudents.removeAll(this.tlStudents.get());
            this.tlStudents.get().clear();
        }
    }
}

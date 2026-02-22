package com.project.qa.steps;

import com.project.qa.api.controller.actions.BookingActions;
import com.project.qa.helpers.managers.student.StudentsManager;
import com.project.qa.helpers.models.bookings.Booking;
import io.cucumber.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;

public class DataGeneratorStepDefs extends AbstractStepDefs {
    @Autowired private BookingActions bookingActions;
    @Autowired private StudentsManager studentsManager;

    @And("Generate Student data based on created booking.")
    public void generateStudentDataBasedOnCreatedBooking() {
        Booking booking = threadVarsHashMap.get(TestKeyword.BOOKING);
        threadVarsHashMap.put(TestKeyword.STUDENT_DATA, studentsManager.generateRandomValidStudentDataWithBookingData(booking));
    }
}

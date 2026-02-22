package com.project.qa.steps;

import com.project.qa.api.controller.actions.BookingActions;
import com.project.qa.helpers.models.bookings.Booking;
import io.cucumber.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;

public class APIStepDefs extends AbstractStepDefs {
    @Autowired private BookingActions bookingActions;

    @And("^Booking is created\\.$")
    public void createBookingIfNotCreated() {
        Booking booking = bookingActions.getCreatedBooking();
        threadVarsHashMap.put(TestKeyword.BOOKING, booking);
    }
}

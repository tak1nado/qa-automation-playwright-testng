package com.project.qa.api.controller.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.project.qa.api.controller.managers.BookingsControllerApi;
import com.project.qa.helpers.exceptions.InstanceAlreadyPickedException;
import com.project.qa.helpers.exceptions.NotFoundException;
import com.project.qa.helpers.managers.bookings.BookingsManager;
import com.project.qa.helpers.models.bookings.Booking;
import com.project.qa.helpers.models.bookings.BookingData;
import com.project.qa.helpers.models.bookings.BookingResponse;
import com.project.qa.helpers.user.engine.UserSessions;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Log
@Component
public class BookingActions {
    @Autowired private UserSessions userSessions;
    @Autowired private BookingsManager bookingsManager;
    @Autowired private BookingsControllerApi bookingsControllerApi;

    @Step("Get existed booking or create new.")
    public synchronized Booking getCreatedBooking() {
        try {
            return bookingsManager.pick(bookingsManager.getNotPickedBookings().stream()
                    .findAny().orElseGet(() -> {
                        BookingData newBookingData = bookingsManager.generateRandomValidBookingData();
                        Response response = bookingsControllerApi.createNewBooking(newBookingData);
                        try {
                            BookingResponse bookingResponse = JsonMapper.builder()
                                    .findAndAddModules().build()
                                    .readValue(response.asString(), BookingResponse.class);

                            assertThat(bookingResponse.getBookingid())
                                    .as("Check that 'bookingid' is not null and positive")
                                    .isNotNull()
                                    .isPositive();

                            return bookingsManager.createInstance(bookingResponse.getBookingid(), newBookingData);
                        } catch (JsonProcessingException e) {
                            log.warning("Booking is not created correctly: " + e.getMessage());
                            throw new NotFoundException("Booking is not created correctly: " + e.getMessage());
                        }
                    }));
        } catch (InstanceAlreadyPickedException ex) {
            return getCreatedBooking();
        }
    }
}

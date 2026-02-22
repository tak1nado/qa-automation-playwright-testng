package com.project.qa.api.suites;

import com.project.qa.api.controller.BookerApi;
import com.project.qa.api.controller.actions.BookingActions;
import com.project.qa.helpers.managers.bookings.BookingsManager;
import com.project.qa.helpers.models.bookings.Booking;
import com.project.qa.runners.APITestsRunner;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.java.Log;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

@Log
@Test(groups = "api")
@Epic("Bookings")
@Feature("Search for booking API feature")
public class GetBookingTests extends APITestsRunner {
    @Autowired private BookingsManager bookingsManager;
    @Autowired private BookingActions bookingActions;
    @Autowired private BookerApi bookerApi;

    @Test
    @Description("Search booking by id api.")
    public void searchBookingResponseValidation() {
        Booking booking = bookingActions.getCreatedBooking();

        log.info("Search for booking by id: " + booking.getId());

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .pathParam("id", booking.getId())
                .get("/booking/{id}");

        log.info("Get booking response: " + response.asString());

        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("firstname", equalTo(booking.getBookingData().getFirstname()))
                .body("lastname", equalTo(booking.getBookingData().getLastname()))
                .body("totalprice", equalTo(booking.getBookingData().getTotalPrice()))
                .body("depositpaid", equalTo(booking.getBookingData().getIsDepositPaid()))
                .body("bookingdates.checkin", equalTo(booking.getBookingData().getCheckInDateString()))
                .body("bookingdates.checkout", equalTo(booking.getBookingData().getCheckOutDateString()))
                .body("additionalneeds", equalTo(booking.getBookingData().getAdditionalNeeds()));
    }
}

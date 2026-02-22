package com.project.qa.api.suites;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.qa.api.controller.BookerApi;
import com.project.qa.api.controller.actions.BookingActions;
import com.project.qa.helpers.managers.bookings.BookingsManager;
import com.project.qa.helpers.models.bookings.Booking;
import com.project.qa.helpers.models.bookings.BookingData;
import com.project.qa.helpers.models.bookings.BookingResponse;
import com.project.qa.helpers.user.engine.UserSessions;
import com.project.qa.runners.APITestsRunner;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import lombok.extern.java.Log;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

@Log
@Test(groups = "api")
@Epic("Bookings")
@Feature("Update booking API feature")
public class UpdateBookingTests extends APITestsRunner {
    @Autowired private BookingsManager bookingsManager;
    @Autowired private BookingActions bookingActions;
    @Autowired private BookerApi bookerApi;
    private static final String AUTH_COOKIE_TOKEN_NAME = "token";

    @Test
    @Description("Update booking with valid date api.")
    public void fullUpdateBookingResponseValidation() throws JsonProcessingException {
        Booking booking = bookingActions.getCreatedBooking();
        BookingData newBookingData = bookingsManager.generateRandomValidBookingData();

        log.info("Update existed booking: " + booking);

        ObjectNode bookingDatesNode = new ObjectMapper().createObjectNode();
        bookingDatesNode.put("checkin", newBookingData.getCheckInDateString());
        bookingDatesNode.put("checkout", newBookingData.getCheckOutDateString());

        ObjectNode jsonBody = new ObjectMapper().createObjectNode();
        jsonBody.put("firstname", newBookingData.getFirstname());
        jsonBody.put("lastname", newBookingData.getLastname());
        jsonBody.put("totalprice", newBookingData.getTotalPrice());
        jsonBody.put("depositpaid", newBookingData.getIsDepositPaid());
        jsonBody.set("bookingdates", bookingDatesNode);
        jsonBody.put("additionalneeds", newBookingData.getAdditionalNeeds());

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .cookie(new Cookie.Builder(AUTH_COOKIE_TOKEN_NAME, authActions.getAccessToken(adminUserSession)).build())
                .body(jsonBody)
                .pathParam("id", booking.getId())
                .put("/booking/{id}");

        log.info("Full Update booking response: " + response.asString());

        BookingResponse.BookingDataResponse bookingDataResponse = JsonMapper.builder()
                .findAndAddModules().build()
                .readValue(response.asString(), BookingResponse.BookingDataResponse.class);

        //As example of assertJ usage
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(bookingDataResponse.getAdditionalneeds()).isEqualTo(newBookingData.getAdditionalNeeds())
                .as("Additional needs data should be equal.");
        softly.assertAll();

        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("firstname", equalTo(newBookingData.getFirstname()))
                .body("lastname", equalTo(newBookingData.getLastname()))
                .body("totalprice", equalTo(newBookingData.getTotalPrice()))
                .body("depositpaid", equalTo(newBookingData.getIsDepositPaid()))
                .body("bookingdates.checkin", equalTo(newBookingData.getCheckInDateString()))
                .body("bookingdates.checkout", equalTo(newBookingData.getCheckOutDateString()))
                .body("additionalneeds", equalTo(newBookingData.getAdditionalNeeds()));

        booking.update(newBookingData);
    }

    @Test
    @Description("Partially update booking with valid date api.")
    public void partialUpdateBookingResponseValidation() throws JsonProcessingException {
        Booking booking = bookingActions.getCreatedBooking();
        BookingData newBookingData = booking.getBookingData().getCopy();
        newBookingData.setFirstname(booking.getBookingData().getFirstname() + "updated");

        log.info("Partially Update existed booking: " + booking);

        ObjectNode jsonBody = new ObjectMapper().createObjectNode();
        jsonBody.put("firstname", newBookingData.getFirstname());

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .cookie(new Cookie.Builder(AUTH_COOKIE_TOKEN_NAME, authActions.getAccessToken(adminUserSession)).build())
                .body(jsonBody)
                .pathParam("id", booking.getId())
                .patch("/booking/{id}");

        log.info("Partial Update booking response: " + response.asString());

        BookingResponse.BookingDataResponse bookingDataResponse = JsonMapper.builder()
                .findAndAddModules().build()
                .readValue(response.asString(), BookingResponse.BookingDataResponse.class);

        //As example of assertJ usage
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(bookingDataResponse.getFirstname())
                .as("Firstname data should be equal.")
                .isEqualTo(newBookingData.getFirstname());
        softly.assertAll();

        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("firstname", equalTo(newBookingData.getFirstname()))
                .body("lastname", equalTo(booking.getBookingData().getLastname()))
                .body("totalprice", equalTo(booking.getBookingData().getTotalPrice()))
                .body("depositpaid", equalTo(booking.getBookingData().getIsDepositPaid()))
                .body("bookingdates.checkin", equalTo(booking.getBookingData().getCheckInDateString()))
                .body("bookingdates.checkout", equalTo(booking.getBookingData().getCheckOutDateString()))
                .body("additionalneeds", equalTo(booking.getBookingData().getAdditionalNeeds()));

        booking.update(newBookingData);
    }
}

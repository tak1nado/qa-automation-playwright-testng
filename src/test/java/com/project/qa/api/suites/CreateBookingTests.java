package com.project.qa.api.suites;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.qa.api.controller.BookerApi;
import com.project.qa.helpers.managers.bookings.BookingsManager;
import com.project.qa.helpers.models.bookings.BookingData;
import com.project.qa.helpers.models.bookings.BookingResponse;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;

@Log
@Test(groups = "api")
@Epic("Bookings")
@Feature("Create booking API feature")
public class CreateBookingTests extends APITestsRunner {
    @Autowired private BookingsManager bookingsManager;
    @Autowired private BookerApi bookerApi;

    @Test
    @Description("Create new valid booking with api.")
    public void createNewBookingResponseValidation() throws JsonProcessingException {
        BookingData newBookingData = bookingsManager.generateRandomValidBookingData();

        log.info("Create new booking: " + newBookingData);

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
                .body(jsonBody)
                .post("/booking");

        log.info("Create new booking response: " + response.asString());

        BookingResponse bookingResponse = JsonMapper.builder()
                .findAndAddModules().build()
                .readValue(response.asString(), BookingResponse.class);

        assertThat(bookingResponse.getBookingid())
                .as("Check that 'bookingid' is not null and positive")
                .isNotNull()
                .isPositive();

        bookingsManager.createInstance(bookingResponse.getBookingid(), newBookingData);

        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("bookingid", notNullValue())
                .body("booking.firstname", equalTo(newBookingData.getFirstname()))
                .body("booking.lastname", equalTo(newBookingData.getLastname()))
                .body("booking.totalprice", equalTo(newBookingData.getTotalPrice()))
                .body("booking.depositpaid", equalTo(newBookingData.getIsDepositPaid()))
                .body("booking.bookingdates.checkin", equalTo(newBookingData.getCheckInDateString()))
                .body("booking.bookingdates.checkout", equalTo(newBookingData.getCheckOutDateString()))
                .body("booking.additionalneeds", equalTo(newBookingData.getAdditionalNeeds()));
    }
}

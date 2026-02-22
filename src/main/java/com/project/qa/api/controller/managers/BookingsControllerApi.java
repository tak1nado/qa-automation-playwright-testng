package com.project.qa.api.controller.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.qa.api.controller.BookerApi;
import com.project.qa.helpers.managers.users.UsersManager;
import com.project.qa.helpers.models.bookings.BookingData;
import com.project.qa.helpers.user.engine.UserSession;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log
@Component
public class BookingsControllerApi {
    @Autowired private BookerApi bookerApi;
    @Autowired private UsersManager usersManager;
    private static final String AUTH_HEADER_NAME = "authorization";
    private static final String AUTH_HEADER_BEARER_TEXT = "Bearer ";
    private static final String AUTH_COOKIE_TOKEN_NAME = "token";

    @Step("Create new booking {0}")
    public Response createNewBooking(BookingData newBookingData) {
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

        return response;
    }

    @Step("Get booking data/response by id {0}")
    public Response getBookingDataById(int id) {
        log.info("Get booking request by: " + id);

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .get("/booking/{id}");

        log.info("Get booking response: " + response.asString());

        return response;
    }

    @Step("Get all bookingIds data/response")
    public Response getAllBookingIds() {
        log.info("Get all bookings request.");

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .get("/booking");

        log.info("Get all bookings response: " + response.asString());

        return response;
    }

    @Step("Get all bookingIds data/response")
    public Response getAllBookingIdsFilteringBy(Map<String, String> params) {
        log.info("Get all bookings request.");

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .queryParams(params)
                .get("/booking");

        log.info("Get all bookings response: " + response.asString());

        return response;
    }

    @Step("Full update booking data {2} with id {1}")
    public Response fullUpdateBookingById(UserSession userSession, int id, BookingData bookingData) {
        log.info(String.format("Full Update booking request by id: %s, by editor: %s, booking data: %s", id, userSession.getUser().getUsername(), bookingData));

        ObjectNode bookingDatesNode = new ObjectMapper().createObjectNode();
        bookingDatesNode.put("checkin", bookingData.getCheckInDateString());
        bookingDatesNode.put("checkout", bookingData.getCheckOutDateString());

        ObjectNode jsonBody = new ObjectMapper().createObjectNode();
        jsonBody.put("firstname", bookingData.getFirstname());
        jsonBody.put("lastname", bookingData.getLastname());
        jsonBody.put("totalprice", bookingData.getTotalPrice());
        jsonBody.put("depositpaid", bookingData.getIsDepositPaid());
        jsonBody.set("bookingdates", bookingDatesNode);
        jsonBody.put("additionalneeds", bookingData.getAdditionalNeeds());

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .cookie(new Cookie.Builder(AUTH_COOKIE_TOKEN_NAME, userSession.getToken().getIdToken()).build())
                .body(jsonBody)
                .pathParam("id", id)
                .put("/booking/{id}");

        log.info("Full Update booking response: " + response.asString());

        return response;
    }

    @Step("Partial update booking data {2} with id {1}")
    public Response partialUpdateBookingById(UserSession userSession, int id, BookingData bookingData) {
        log.info(String.format("Partial Update booking request by id: %s, by editor: %s, booking data: %s", id, userSession.getUser().getUsername(), bookingData));

        ObjectNode bookingDatesNode = new ObjectMapper().createObjectNode();
        if (bookingData.getCheckIn() != null) {
            bookingDatesNode.put("checkin", bookingData.getCheckInDateString());
        }
        if (bookingData.getCheckOut() != null) {
            bookingDatesNode.put("checkout", bookingData.getCheckOutDateString());
        }

        ObjectNode jsonBody = new ObjectMapper().createObjectNode();
        if (bookingData.getFirstname() != null) {
            jsonBody.put("firstname", bookingData.getFirstname());
        }
        if (bookingData.getFirstname() != null) {
            jsonBody.put("lastname", bookingData.getLastname());
        }
        if (bookingData.getTotalPrice() != null) {
            jsonBody.put("totalprice", bookingData.getTotalPrice());
        }
        if (bookingData.getIsDepositPaid() != null) {
            jsonBody.put("depositpaid", bookingData.getIsDepositPaid());
        }
        if (!bookingDatesNode.isEmpty()) {
            jsonBody.set("bookingdates", bookingDatesNode);
        }
        if (bookingData.getAdditionalNeeds() != null) {
            jsonBody.put("additionalneeds", bookingData.getAdditionalNeeds());
        }

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .cookie(new Cookie.Builder(AUTH_COOKIE_TOKEN_NAME, userSession.getToken().getIdToken()).build())
                .body(jsonBody)
                .pathParam("id", id)
                .patch("/booking/{id}");

        log.info("Partial Update booking response: " + response.asString());

        return response;
    }

    @Step("Delete booking by id {1}")
    public Response deleteBookingById(UserSession userSession, int id) {
        log.info("Delete booking request by: " + id);

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .cookie(new Cookie.Builder(AUTH_COOKIE_TOKEN_NAME, userSession.getToken().getIdToken()).build())
                .pathParam("id", id)
                .delete("/booking/{id}");

        log.info("Delete booking response: " + response.asString());

        return response;
    }

}

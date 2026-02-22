package com.project.qa.api.suites;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.qa.api.controller.BookerApi;
import com.project.qa.backoffice.user.BackofficeUserRole;
import com.project.qa.helpers.managers.users.UsersManager;
import com.project.qa.helpers.models.users.User;
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

import static org.hamcrest.Matchers.*;

@Log
@Test(groups = "api")
@Epic("Authentication")
@Feature("Restful authentication feature")
public class AuthenticationTests extends APITestsRunner {
    @Autowired private UsersManager usersManager;
    @Autowired private BookerApi bookerApi;

    @Test
    @Description("Check that Admin is able to authenticate.")
    public void userAuthResponseValidation() {
        User admin = usersManager.getUserByRole(BackofficeUserRole.ADMIN);

        log.info("Login as: " + admin);
        ObjectNode jsonBody = new ObjectMapper().createObjectNode();
        jsonBody.put("username", admin.getUsername());
        jsonBody.put("password", admin.getPassword());

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .post("/auth");

        log.info("User is logged in response: " + response);

        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue());
    }

    @Test
    @Description("Check that Admin is not able to authenticate with wrong password.")
    public void userFailedPasswordAuthResponseValidation() {
        User admin = usersManager.getUserByRole(BackofficeUserRole.ADMIN);

        log.info("Login as: " + admin);
        ObjectNode jsonBody = new ObjectMapper().createObjectNode();
        jsonBody.put("username", admin.getUsername());
        jsonBody.put("password", admin.getPassword() + "wrong");

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .post("/auth");

        log.info("User is logged in response: " + response);

        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", nullValue())
                .body("reason", equalTo("Bad credentials"));
    }
}

package com.project.qa.api.controller.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.qa.api.controller.BookerApi;
import com.project.qa.helpers.user.engine.Token;
import com.project.qa.helpers.user.engine.UserSession;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log
@Component
public class AuthenticationControllerApi {
    @Autowired private BookerApi bookerApi;

    @Step("Login with user {0} request")
    public void loginRequestForUserSession(UserSession userSession) {
        log.info("Login as: " + userSession.getUser());
        ObjectNode jsonBody = new ObjectMapper().createObjectNode();
        jsonBody.put("username", userSession.getUser().getUsername());
        jsonBody.put("password", userSession.getUser().getPassword());

        Response response = bookerApi.bookerControllerRequest()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .post("/auth");

        log.info("User is logged in: " + response);
        log.info("Set token to user session.");

        Token token = new Token(response.jsonPath().getString("token"));
        userSession.setToken(token);
    }

}

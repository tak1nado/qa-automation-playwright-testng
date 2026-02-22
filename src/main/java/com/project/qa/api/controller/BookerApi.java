package com.project.qa.api.controller;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookerApi {
    @Autowired private BookerController bookerController;

    public RequestSpecification bookerControllerRequest() {
        return RestAssured.given().baseUri(bookerController.getBaseUrl());
    }
    
}

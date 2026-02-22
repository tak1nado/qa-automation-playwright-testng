package com.project.qa.api.controller;

public class BookerController {
    private final String baseUrl;

    public BookerController(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}

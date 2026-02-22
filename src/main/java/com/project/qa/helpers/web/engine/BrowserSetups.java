package com.project.qa.helpers.web.engine;

import lombok.Getter;

import java.util.Objects;

public class BrowserSetups {
    @Getter private final String hubUrl;
    @Getter private final String browserName;
    @Getter private final boolean headless;

    public BrowserSetups(String hubUrl, String browserName, String headless) {
        this.hubUrl = Objects.equals(hubUrl, "") ? null : hubUrl;
        this.browserName = browserName;
        this.headless = Boolean.parseBoolean(headless);
    }
}

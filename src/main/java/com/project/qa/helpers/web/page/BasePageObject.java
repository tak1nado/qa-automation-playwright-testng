package com.project.qa.helpers.web.page;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.java.Log;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Log
public abstract class BasePageObject extends UIComponent {

    public String getCurrentUrl() {
        return getActor().url();
    }

    public String getDecodedCurrentUrl() {
        return URLDecoder.decode(getCurrentUrl(), StandardCharsets.UTF_8);
    }

    public boolean isOpened() {
        Allure.step("Is page opened: " + getPageUrl() + "?");
        return getCurrentUrl().equals(getPageUrl());
    }

    public void open() {
        open(getPageUrl());
    }

    @Step("Open page {0}.")
    protected void open(String url) {
        getActor().navigate(url);
    }

    public abstract String getPageUrl();

    @Step("Refresh page.")
    public void refreshPage() {
        getActor().reload();
    }
}
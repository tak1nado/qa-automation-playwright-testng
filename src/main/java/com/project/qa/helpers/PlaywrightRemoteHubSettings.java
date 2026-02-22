package com.project.qa.helpers;

import lombok.Getter;

import java.net.MalformedURLException;
import java.net.URL;

public class PlaywrightRemoteHubSettings {

    @Getter private String hubUrl;

    public PlaywrightRemoteHubSettings(String hubUrl) {
        if (hubUrl != null && !hubUrl.isEmpty())
            this.hubUrl = hubUrl;
    }
}

package com.project.qa.runners;

import com.project.qa.helpers.ThreadVarsHashMap;
import com.project.qa.helpers.web.engine.BrowserSessions;
import com.project.qa.helpers.web.engine.BrowserTabSession;
import com.project.qa.steps.TestKeyword;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;

import static org.testng.Assert.assertTrue;

@Log
public class CucumberHooks {
    @Autowired private BrowserSessions browserSessions;
    @Autowired private ThreadVarsHashMap<TestKeyword> threadVarsHashMap;

    @Value("#{new Boolean('${console.errors:false}'.trim())}")
    boolean consoleErrorsEnabled;

    @After(order = 2)
    public synchronized void onFailure(Scenario scenario) {
        if (scenario.isFailed()) {
            captureScreenshot(scenario);
            if (!browserSessions.getActiveBrowserSession().getConsoleErrorsList().isEmpty()) {
                attachBrowserLogs();
            }
        }
    }

    @After(order = 3)
    public synchronized void checkForBrowserErrors() {
        if (consoleErrorsEnabled) {
            if (!browserSessions.getActiveBrowserSession().getConsoleErrorsList().isEmpty()) {
                attachBrowserLogs();
            }
            assertTrue(browserSessions.getActiveBrowserSession().getConsoleErrorsList().isEmpty(),
                    "Critical browser errors detected:\n" + String.join("\n--------------\n", browserSessions.getActiveBrowserSession().getConsoleErrorsList()));
        }
        browserSessions.getActiveBrowserSession().getConsoleErrorsList().clear();
    }

    @After(order = 100)
    public synchronized void clearThreadVars() {
        threadVarsHashMap.clear();
    }

    public void captureScreenshot(Scenario scenario) {
        try {
            BrowserTabSession browserTabSession = browserSessions.getActiveBrowserTabSession();
            byte[] screenshotBytes = browserTabSession.getPage().screenshot();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(screenshotBytes);
            Allure.addAttachment(scenario.getName(), inputStream);
        } catch (Exception e) {
            log.warning("Failed to capture screenshot: " + e.getMessage());
        }
    }

    //TODO fix saving errors in console
    private void attachBrowserLogs() {
        String formattedLogs = String.join("\n--------------\n", browserSessions.getActiveBrowserSession().getConsoleErrorsList());
        Allure.attachment("Browser Console Errors: ", formattedLogs);
    }
}
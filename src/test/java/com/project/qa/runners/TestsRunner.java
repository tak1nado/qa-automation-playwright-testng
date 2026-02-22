package com.project.qa.runners;

import com.project.qa.api.controller.BookerController;
import com.project.qa.api.controller.actions.AuthActions;
import com.project.qa.backoffice.user.BackofficeUserRole;
import com.project.qa.helpers.managers.bookings.BookingsManager;
import com.project.qa.helpers.user.engine.UserSession;
import com.project.qa.helpers.web.engine.BrowserSessions;
import com.project.qa.storefront.StorefrontCockpit;
import io.cucumber.spring.CucumberContextConfiguration;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Log
@CucumberContextConfiguration
@ContextConfiguration(locations = {"classpath:spring-application-context.xml"})
public class TestsRunner extends AbstractTestNGSpringContextTests {
    @Autowired private BookingsManager bookingsManager;
    @Autowired private BrowserSessions browserSessions;
    @Autowired private StorefrontCockpit storefrontCockpit;
    @Autowired private BookerController bookerController;
    @Autowired protected AuthActions authActions;
    protected UserSession adminUserSession;

    @BeforeSuite(alwaysRun = true)
    public void globalInitSuite() throws Exception {
        super.springTestContextBeforeTestClass();
        super.springTestContextPrepareTestInstance();

        RestAssured.replaceFiltersWith(new AllureRestAssured());
        log.info("🌱 Initializing global Spring context");
    }

    @BeforeClass(alwaysRun = true)
    public void prepareData() {
        adminUserSession = authActions.loginAs(BackofficeUserRole.ADMIN);
    }

    @AfterSuite(alwaysRun = true)
    public void globalCleanupSuite() {
        log.info("🧹 Global cleanup");
        bookingsManager.deleteAllCreatedBookings(adminUserSession);
        createAllureReportEnvironmentVariables();
        browserSessions.dismissAll();
    }

    private void createAllureReportEnvironmentVariables() {
        try (FileOutputStream fos = new FileOutputStream("target/allure-results/environment.properties")) {
            Properties properties = new Properties();
            properties.setProperty("Environment: ", storefrontCockpit.getBaseUrl());
            properties.setProperty("Restful endpoint: ", bookerController.getBaseUrl());
            if (browserSessions.getBrowserInfo() != null) {
                ofNullable(browserSessions.getBrowserInfo().getBrowserName()).ifPresent(p -> properties.setProperty("Browser: ", p));
                ofNullable(browserSessions.getBrowserInfo().getBrowserVersion()).ifPresent(p -> properties.setProperty("Browser version: ", p));
            }
            ofNullable(System.getProperty("os.name")).ifPresent(p -> properties.setProperty("OS name: ", p));
            ofNullable(System.getenv("GITLAB_USER_NAME")).ifPresent(p -> properties.setProperty("Gitlab user name: ", p));

            properties.store(fos, EMPTY);
        } catch (IOException e) {
            log.warning("IO problem when writing allure properties file: " + e.getMessage());
        }
    }
}

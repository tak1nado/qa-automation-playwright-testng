package com.project.qa.runners;

import com.project.qa.helpers.managers.bookings.BookingsManager;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

@Log
public class APITestsRunner extends TestsRunner {
    @Autowired protected BookingsManager bookingsManager;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethodLog(Method method) {
        log.info("START TEST METHOD: " + method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethodCleanUp(Method method) {
        bookingsManager.unpickThreadBookings();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethodLog(Method method) {
        log.info("FINISH TEST METHOD: " + method.getName());
    }
}

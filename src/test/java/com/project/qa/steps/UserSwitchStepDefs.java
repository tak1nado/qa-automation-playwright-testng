package com.project.qa.steps;

import com.project.qa.backoffice.user.BackofficeUserRole;
import com.project.qa.helpers.managers.users.UsersManager;
import com.project.qa.helpers.web.engine.BrowserSessions;
import com.project.qa.storefront.user.StorefrontUserRole;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class UserSwitchStepDefs extends AbstractStepDefs {

    @Autowired private BrowserSessions browserSessions;
    @Autowired private UsersManager usersManager;

    @Given("Switch to Backoffice cockpit Admin user.")
    public void switchToBackofficeAdmin() {
        browserSessions.setBrowserSessionActive(BackofficeUserRole.ADMIN);
    }

    @Given("Switch to Storefront cockpit Shopper user.")
    public void switchToStorefrontAsShopper() {
        browserSessions.setBrowserSessionActive(StorefrontUserRole.SHOPPER);
    }

    @Given("Switch to Storefront cockpit guest user.")
    public void switchToStorefrontAsGuest() {
        browserSessions.setBrowserSessionActive(StorefrontUserRole.GUEST);
    }
}

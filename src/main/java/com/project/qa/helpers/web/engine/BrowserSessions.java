package com.project.qa.helpers.web.engine;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.project.qa.helpers.exceptions.NotFoundException;
import com.project.qa.helpers.models.users.UserRole;
import com.project.qa.helpers.user.engine.UserSessions;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Log
@Component
public class BrowserSessions {
    @Autowired UserSessions userSessions;
    @Autowired BrowserThreadTestSetups browserThreadTestSetups;
    private final Playwright playwright = Playwright.create();
    private Browser browser;

    private final InheritableThreadLocal<HashMap<UserRole, BrowserSession>> tlBrowserSessionsMap = new InheritableThreadLocal<>();

    public synchronized void setBrowserSession(String hubUrl, String browserName, boolean headless, UserRole userRole) {
        if (browserName.equalsIgnoreCase("chrome") && hubUrl == null) {
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
            BrowserSession browserSession = new BrowserSession(browser.newContext(
                    new Browser.NewContextOptions().setViewportSize(1920, 1080))
            );
            tlBrowserSessionsMap.get().put(userRole, browserSession);
            browserSession.openNewTab();
        } else if (browserName.equalsIgnoreCase("chrome") && hubUrl != null) {
            browser = playwright.chromium().connect(hubUrl);
            BrowserSession browserSession = new BrowserSession(browser.newContext(
                    new Browser.NewContextOptions().setViewportSize(1920, 1080))
            );
            tlBrowserSessionsMap.get().put(userRole, browserSession);
            browserSession.openNewTab();
        }
    }

    public synchronized void setBrowserSessionActive(UserRole userRole) {
        if (tlBrowserSessionsMap.get() == null) {
            tlBrowserSessionsMap.set(new HashMap<>());
            setBrowserSession(browserThreadTestSetups.getBrowserSetups().getHubUrl(),
                    browserThreadTestSetups.getBrowserSetups().getBrowserName(),
                    browserThreadTestSetups.getBrowserSetups().isHeadless(),
                    userRole);
            tlBrowserSessionsMap.get().get(userRole).setActive(true);
            userSessions.setActiveUserSession(userRole);
        } else if (!userSessions.getActiveUserSession().getUserRole().equals(userRole)) {
            tlBrowserSessionsMap.get().forEach((userRole1, browserSession) -> browserSession.setActive(false));
            if (tlBrowserSessionsMap.get().get(userRole) == null)
                setBrowserSession(browserThreadTestSetups.getBrowserSetups().getHubUrl(),
                        browserThreadTestSetups.getBrowserSetups().getBrowserName(),
                        browserThreadTestSetups.getBrowserSetups().isHeadless(),
                        userRole);
            tlBrowserSessionsMap.get().get(userRole).setActive(true);
            userSessions.setActiveUserSession(userRole);
        }
        userSessions.getActiveUserSession().setCookies(tlBrowserSessionsMap.get().get(userRole).getBrowserContext().cookies());
    }


    public synchronized BrowserContext getActiveBrowserSessionContext() {
        if (tlBrowserSessionsMap.get() == null) {
            log.info("Current test Thread doesn't have any browsers");
            return null;
        }
        return tlBrowserSessionsMap.get().values().stream()
                .filter(BrowserSession::isActive)
                .map(BrowserSession::getBrowserContext)
                .findAny().orElse(null);
    }

    public synchronized BrowserSession getActiveBrowserSession() {
        if (tlBrowserSessionsMap.get() == null) {
            log.info("Current test Thread doesn't have any browser sessions");
            return null;
        }
        return tlBrowserSessionsMap.get().values().stream()
                .filter(BrowserSession::isActive)
                .findAny().orElse(null);
    }

    public synchronized BrowserTabSession getActiveBrowserTabSession() {
        if (tlBrowserSessionsMap.get() == null) {
            log.info("Current test Thread doesn't have any browser sessions");
            return null;
        }
        return tlBrowserSessionsMap.get().values().stream()
                .filter(BrowserSession::isActive)
                .map(browserSession -> browserSession.getBrowserTabSessions().stream()
                        .filter(BrowserTabSession::isActive)
                        .findAny().orElseThrow(() -> new NotFoundException("There are no active browser tabs")))
                .findAny().orElse(null);
    }

    public void dismissAll() {
        if (tlBrowserSessionsMap.get() != null) {
            tlBrowserSessionsMap.get().values().forEach(BrowserSession::dismiss);
            tlBrowserSessionsMap.get().clear();
        }
        if (browser != null) {
            browser.close();
        }
    }

    public BrowserInfo getBrowserInfo() {
        if (browser == null) {
            return null;
        }
        return new BrowserInfo(browser.browserType().name(), browser.version());
    }
}

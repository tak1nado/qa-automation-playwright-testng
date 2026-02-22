package com.project.qa.helpers.web.engine;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class BrowserSession {
    @Getter private final BrowserContext browserContext;
    @Getter private final List<BrowserTabSession> browserTabSessions = new ArrayList<>();
    @Getter @Setter private boolean isActive = false;
    @Getter private final List<String> consoleErrorsList = new ArrayList<>();

    public BrowserSession(BrowserContext browserContext) {
        this.browserContext = browserContext;
        this.browserContext.onConsoleMessage(msg -> {
            if (msg.type().equalsIgnoreCase("ERROR")) {
                consoleErrorsList.add(String.format("[ERROR] %s (URL: %s)", msg.text(), msg.location()));
            }
        });
    }

    public void openNewTab() {
        Page page = browserContext.newPage();
        BrowserTabSession browserTabSession = new BrowserTabSession(page);
        browserTabSession.setActive(true);
        browserTabSessions.add(browserTabSession);
    }

    //only for BrowserSessions usage
    public void dismiss() {
        browserContext.close();
    }
}

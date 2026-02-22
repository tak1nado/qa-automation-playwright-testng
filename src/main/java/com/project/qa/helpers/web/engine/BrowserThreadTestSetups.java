package com.project.qa.helpers.web.engine;

import org.springframework.stereotype.Component;

@Component
public class BrowserThreadTestSetups {

    private final InheritableThreadLocal<BrowserSetups> tlBrowserSetups = new InheritableThreadLocal<>();

    public synchronized void setTlBrowserSetups(BrowserSetups browserSetups) {
        tlBrowserSetups.set(browserSetups);
    }

    public synchronized BrowserSetups getBrowserSetups() {
        return tlBrowserSetups.get();
    }
}

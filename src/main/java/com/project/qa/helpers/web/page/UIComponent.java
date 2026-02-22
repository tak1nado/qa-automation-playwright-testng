package com.project.qa.helpers.web.page;

import com.microsoft.playwright.Page;
import com.project.qa.helpers.web.engine.BrowserSessions;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;


@Log
public abstract class UIComponent {

    @Autowired protected BrowserSessions browserSessions;

    protected Page getActor() {
        return browserSessions.getActiveBrowserTabSession().getPage();
    }

}
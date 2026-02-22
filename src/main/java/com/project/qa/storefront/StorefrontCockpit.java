package com.project.qa.storefront;

import com.microsoft.playwright.Page;
import com.project.qa.Cockpit;
import org.springframework.beans.factory.annotation.Autowired;

public class StorefrontCockpit extends Cockpit {
//    @Autowired private StartPage startPage;

    public StorefrontCockpit(String baseUrl) {
        this.baseUrl = baseUrl;
    }

//    @Override
//    public Page getStartPage() {
//        return null;
//    }
}

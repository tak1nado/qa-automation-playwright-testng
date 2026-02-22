package com.project.qa;

import com.microsoft.playwright.Page;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public abstract class Cockpit {
    protected String baseUrl;

//    public abstract Page getStartPage();
}

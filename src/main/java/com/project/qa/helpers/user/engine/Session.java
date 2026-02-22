package com.project.qa.helpers.user.engine;

import com.microsoft.playwright.options.Cookie;
import com.project.qa.helpers.models.users.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

abstract public class Session {
    @Getter protected boolean isLoggedIn = false;
    @Getter @Setter protected boolean isActive = false;
    @Getter protected List<Cookie> cookies;
    @Getter @Setter protected Token token;

    abstract public UserRole getUserRole();

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public void addCookies(List<Cookie> cookies) {
        Set<Cookie> addCookies = cookies.stream()
                .filter(cookie1 -> this.cookies.stream().noneMatch(cookie2 -> cookie1.name.equals(cookie2.name)))
                .collect(Collectors.toSet());

        if (!addCookies.isEmpty())
            this.cookies.addAll(addCookies);
    }

    public void setCookies(List<Cookie> cookies) {
        if (this.cookies == null) {
            this.cookies = cookies;
        } else
            addCookies(cookies);
    }

//    public Cookies getRestAssuredCookies() {
//        CookieAdapter cookieAdapter = new CookieAdapter(ExpiryType.MAXAGE);
//        return new Cookies(this.cookies.stream().map(cookieAdapter::convertToRestAssured).collect(Collectors.toList()));
//    }

    public void clearCookies() {
        this.cookies = null;
    }
}

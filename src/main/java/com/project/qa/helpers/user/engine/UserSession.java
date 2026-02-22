package com.project.qa.helpers.user.engine;

import com.project.qa.helpers.models.users.User;
import com.project.qa.helpers.models.users.UserRole;
import lombok.Getter;

public class UserSession extends Session {
    @Getter private User user;

    public UserSession(User user) {
        this.user = user;
    }

    public UserRole getUserRole() {
        return user.getUserRole();
    }

    public String getEmail() {
        return user.getUsername();
    }

    public String getPassword() {
        return user.getPassword();
    }

    public void setPassword(String password) {
        this.user.setPassword(password);
    }

    public String toString() {
        return user.toString();
    }
}

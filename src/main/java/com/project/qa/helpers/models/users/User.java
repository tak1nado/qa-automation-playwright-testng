package com.project.qa.helpers.models.users;

import com.project.qa.Cockpit;
import lombok.Data;

@Data
public class User {
    private String username;
    private String password;
    private UserRole userRole;
    private Cockpit userCockpit;
    private boolean isTest = true;

    public User(String username, String password, Cockpit userCockpit) {
        this.password = password;
        this.username = username;
        this.userCockpit = userCockpit;
    }

    @Override
    public String toString() {
        return "User: " + this.username + ", Cockpit: " + this.userCockpit.getBaseUrl() + ", Role: " + this.getUserRole();
    }
}

package com.project.qa.helpers.models.users;

public interface UserRole {
    default boolean isTest(){
        return false;
    }

    default String getRoleName() {
        return "";
    }

    default String getRoleCode() {
        return "";
    }
}

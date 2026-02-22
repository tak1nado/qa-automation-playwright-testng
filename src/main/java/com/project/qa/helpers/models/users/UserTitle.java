package com.project.qa.helpers.models.users;

import java.util.Random;

public enum UserTitle {
    MISS("Miss"),
    MR("Mr"),
    REV("Rev."),
    MRS("Mrs"),
    DR("Dr."),
    MS("Ms");

    private final String titleText;

    UserTitle(String titleText) {
        this.titleText = titleText;
    }

    public String getTitleText() {
        return titleText;
    }

    public static UserTitle getRandom() {
        Random generator = new Random();
        int randomIndex = generator.nextInt(UserTitle.values().length);
        return UserTitle.values()[randomIndex];
    }
}

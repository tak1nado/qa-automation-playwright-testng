package com.project.qa.helpers.models.student;

import java.util.Random;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    public String genderText;

    Gender(String genderText) {
        this.genderText = genderText;
    }

    public static Gender getRandom() {
        int rnd = new Random().nextInt(values().length);
        return values()[rnd];
    }
}

package com.project.qa.helpers.models.student;

import java.util.Random;

public enum City {
    DELHI(State.NCR, "Delhi"),
    AGRA(State.UTTAR_PRADESH, "Agra"),
    KARNAL(State.HARYANA, "Karnal"),
    JAISELMER(State.RAJASTHAN, "Jaiselmer");


    public final State state;
    public final String cityText;

    City(State state, String cityText) {
        this.state = state;
        this.cityText = cityText;
    }

    public static City getRandom() {
        int rnd = new Random().nextInt(values().length);
        return values()[rnd];
    }

}

package com.project.qa.helpers.models.student;

public enum State {
    NCR("NCR"),
    UTTAR_PRADESH("Uttar Pradesh"),
    HARYANA("Haryana"),
    RAJASTHAN("Rajasthan");

    public String stateText;

    State(String stateText) {
        this.stateText = stateText;
    }
}

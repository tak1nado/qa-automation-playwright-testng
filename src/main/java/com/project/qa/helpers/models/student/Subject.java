package com.project.qa.helpers.models.student;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public enum Subject {
    ENGLISH("English"),
    MATHS("Maths"),
    ARTS("Arts");

    public String subjectText;

    Subject(String subjectText) {
        this.subjectText = subjectText;
    }

    public static Subject getRandom() {
        int rnd = new Random().nextInt(values().length);
        return values()[rnd];
    }

    public static List<Subject> getRandomUnique(int count) {
        List<Subject> allSubjects = Arrays.stream(Subject.values())
                .collect(Collectors.toList());
        if (count > allSubjects.size()) {
            return allSubjects;
        }
        Collections.shuffle(allSubjects);
        return allSubjects.subList(0, count);
    }
}

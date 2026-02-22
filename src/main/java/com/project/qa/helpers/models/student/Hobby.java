package com.project.qa.helpers.models.student;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public enum Hobby {
    SPORTS("Sports"),
    READING("Reading"),
    MUSIC("Music");

    public String hobbyText;

    Hobby(String hobbyText) {
        this.hobbyText = hobbyText;
    }

    public static Hobby getRandom() {
        int rnd = new Random().nextInt(values().length);
        return values()[rnd];
    }

    public static List<Hobby> getRandomUnique(int count) {
        List<Hobby> allHobbies = Arrays.stream(Hobby.values())
                .collect(Collectors.toList());
        if (count > allHobbies.size()) {
            return allHobbies;
        }
        Collections.shuffle(allHobbies);
        return allHobbies.subList(0, count);
    }
}

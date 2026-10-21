package com.habitforge.habitservice.exception;

import java.util.UUID;

public class HabitNotFoundException extends RuntimeException {

    public HabitNotFoundException(UUID habitId) {
        super("Habit not found: " + habitId);
    }
}
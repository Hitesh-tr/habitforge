package com.habitforge.habitservice.exception;

public class InvalidHabitRequestException extends RuntimeException {

    public InvalidHabitRequestException(String message) {
        super(message);
    }
}
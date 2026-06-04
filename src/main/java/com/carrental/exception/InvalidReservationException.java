package com.carrental.exception;

/**
 * Thrown when a reservation request contains invalid input, such as a
 * null car type, null start date/time, or a non-positive number of days.
 */
public class InvalidReservationException extends RuntimeException {

    public InvalidReservationException(String message) {
        super(message);
    }
}
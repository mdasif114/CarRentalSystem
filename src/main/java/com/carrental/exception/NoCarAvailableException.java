package com.carrental.exception;

/**
 * Thrown when the inventory cannot satisfy a reservation request due to
 * unavailability of the requested car type over the desired date/time
 * range. This is a runtime exception because in this simple example
 * callers typically cannot recover without changing input.
 */
public class NoCarAvailableException extends RuntimeException {

    public NoCarAvailableException(String message) {
        super(message);
    }
}
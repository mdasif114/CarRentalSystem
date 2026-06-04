package com.carrental.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionTest {

    @Test
    void givenMessage_whenCreatingInvalidReservationException_thenMessageIsPreserved() {
        InvalidReservationException exception = new InvalidReservationException("invalid request");

        assertEquals("invalid request", exception.getMessage());
    }

    @Test
    void givenMessage_whenCreatingNoCarAvailableException_thenMessageIsPreserved() {
        NoCarAvailableException exception = new NoCarAvailableException("not available");

        assertEquals("not available", exception.getMessage());
    }
}

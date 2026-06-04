package com.carrental.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservationModelTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 4, 10, 0, 0, 0);
    private static final LocalDateTime END = START.plusDays(2);

    @Test
    void givenValidValues_whenCreatingReservation_thenGettersReturnValues() {
        Reservation reservation = new Reservation("res-1", "vehicle-1", CarType.SEDAN, START, END, 2);

        assertEquals("res-1", reservation.getReservationId());
        assertEquals("vehicle-1", reservation.getVehicleId());
        assertEquals(CarType.SEDAN, reservation.getCarType());
        assertEquals(START, reservation.getStartDateTime());
        assertEquals(END, reservation.getEndDateTime());
        assertEquals(2, reservation.getNumberOfDays());
    }

    @Test
    void givenNullReservationId_whenCreatingReservation_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Reservation(null, "vehicle-1", CarType.SEDAN, START, END, 2));
    }

    @Test
    void givenNullVehicleId_whenCreatingReservation_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Reservation("res-1", null, CarType.SEDAN, START, END, 2));
    }

    @Test
    void givenNullCarType_whenCreatingReservation_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Reservation("res-1", "vehicle-1", null, START, END, 2));
    }

    @Test
    void givenNullStartDateTime_whenCreatingReservation_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Reservation("res-1", "vehicle-1", CarType.SEDAN, null, END, 2));
    }

    @Test
    void givenNullEndDateTime_whenCreatingReservation_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Reservation("res-1", "vehicle-1", CarType.SEDAN, START, null, 2));
    }
}

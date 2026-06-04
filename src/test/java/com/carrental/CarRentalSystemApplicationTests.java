package com.carrental;

import com.carrental.exception.InvalidReservationException;
import com.carrental.exception.NoCarAvailableException;
import com.carrental.model.CarType;
import com.carrental.model.Reservation;
import com.carrental.model.ReservationRequest;
import com.carrental.service.CarRentalSystem;
import com.carrental.vehicle.SUV;
import com.carrental.vehicle.Sedan;
import com.carrental.vehicle.Van;
import com.carrental.vehicle.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarRentalSystemApplicationTests {

    @Test
    void shouldReserveSedanWhenAvailable() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        LocalDateTime start = LocalDateTime.of(2026, 6, 4, 10, 15, 30, 250_000_000);
        ReservationRequest request = new ReservationRequest(CarType.SEDAN, start, 2);
        Reservation reservation = system.reserve(request);
        assertNotNull(reservation);
        assertEquals(CarType.SEDAN, reservation.getCarType());
        // verify reservation is stored
        assertEquals(1, system.getReservationsForVehicle(reservation.getVehicleId()).size());
    }

    @Test
    void shouldRejectSedanReservationWhenAllSedansBooked() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        LocalDateTime start = LocalDateTime.of(2026, 6, 4, 10, 15, 30, 250_000_000);
        // First reservation should succeed
        system.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        // Second overlapping reservation should fail
        Executable secondReservation = () -> system.reserve(new ReservationRequest(CarType.SEDAN, start.plusHours(1), 2));
        assertThrows(NoCarAvailableException.class, secondReservation);
    }

    @Test
    void shouldAllowReservationWhenDatesDoNotOverlap() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        LocalDateTime start = LocalDateTime.of(2026, 6, 4, 10, 15, 30, 250_000_000);
        Reservation r1 = system.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        // Next reservation starts exactly when the first ends
        Reservation r2 = system.reserve(new ReservationRequest(CarType.SEDAN, r1.getEndDateTime(), 1));
        assertNotNull(r2);
        assertEquals(CarType.SEDAN, r2.getCarType());
        assertNotEquals(r1.getReservationId(), r2.getReservationId());
    }

    @Test
    void inventoryIndependenceAcrossTypes() {
        // one of each type
        CarRentalSystem system = new CarRentalSystem(Map.of(
                CarType.SEDAN, 1,
                CarType.SUV, 1,
                CarType.VAN, 1
        ));
        LocalDateTime start = LocalDateTime.of(2026, 6, 4, 10, 0, 0, 0);
        // Book all three types for overlapping periods
        Reservation sedanReservation = system.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        Reservation suvReservation = system.reserve(new ReservationRequest(CarType.SUV, start, 2));
        Reservation vanReservation = system.reserve(new ReservationRequest(CarType.VAN, start, 2));
        assertNotNull(sedanReservation);
        assertNotNull(suvReservation);
        assertNotNull(vanReservation);
        // Attempt to book another sedan should fail because sedan inventory is exhausted
        assertThrows(NoCarAvailableException.class,
                () -> system.reserve(new ReservationRequest(CarType.SEDAN, start.plusHours(1), 1)));
        // Booking of SUV and Van should also fail for overlapping because each has only one
        assertThrows(NoCarAvailableException.class,
                () -> system.reserve(new ReservationRequest(CarType.SUV, start.plusHours(1), 1)));
        assertThrows(NoCarAvailableException.class,
                () -> system.reserve(new ReservationRequest(CarType.VAN, start.plusHours(1), 1)));
    }

    @Test
    void shouldRejectInvalidDurationZero() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        LocalDateTime start = LocalDateTime.now();
        assertThrows(InvalidReservationException.class,
                () -> system.reserve(new ReservationRequest(CarType.SEDAN, start, 0)));
    }

    @Test
    void shouldRejectInvalidDurationNegative() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        LocalDateTime start = LocalDateTime.now();
        assertThrows(InvalidReservationException.class,
                () -> system.reserve(new ReservationRequest(CarType.SEDAN, start, -3)));
    }

    @Test
    void shouldRejectNullCarType() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        LocalDateTime start = LocalDateTime.now();
        assertThrows(InvalidReservationException.class,
                () -> system.reserve(new ReservationRequest(null, start, 1)));
    }

    @Test
    void shouldRejectNullStartDateTime() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        assertThrows(InvalidReservationException.class,
                () -> system.reserve(new ReservationRequest(CarType.SEDAN, null, 1)));
    }

    @Test
    void boundaryCaseNonOverlappingAtEnd() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        LocalDateTime start = LocalDateTime.of(2026, 6, 4, 10, 15, 30, 250_000_000);
        Reservation first = system.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        // Second reservation starts exactly at first reservation's end time
        Reservation second = system.reserve(new ReservationRequest(CarType.SEDAN, first.getEndDateTime(), 2));
        assertNotNull(second);
    }

    @Test
    void millisecondOverlapShouldNotBeAllowed() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        LocalDateTime start = LocalDateTime.of(2026, 6, 4, 10, 15, 30, 250_000_000);
        Reservation first = system.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        // Second reservation overlaps by 1 millisecond: start 1 ms before the end of first
        LocalDateTime overlappingStart = first.getEndDateTime().minus(1, ChronoUnit.MILLIS);
        Executable secondReservation = () -> system.reserve(new ReservationRequest(CarType.SEDAN, overlappingStart, 2));
        assertThrows(NoCarAvailableException.class, secondReservation);
    }

    @Test
    void millisecondPrecisionPreserved() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        // 250123456 nanoseconds will be truncated to 250000000 (250 ms)
        LocalDateTime start = LocalDateTime.of(2026, 6, 4, 10, 15, 30, 250_123_456);
        Reservation reservation = system.reserve(new ReservationRequest(CarType.SEDAN, start, 1));
        assertEquals(250_000_000, reservation.getStartDateTime().getNano());
    }

    @Test
    void endDateTimeCalculatedCorrectly() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        LocalDateTime start = LocalDateTime.of(2026, 6, 4, 10, 15, 30, 250_000_000);
        Reservation reservation = system.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        // end should be start + 2 days, preserving milliseconds
        LocalDateTime expectedEnd = start.truncatedTo(ChronoUnit.MILLIS).plusDays(2);
        assertEquals(expectedEnd, reservation.getEndDateTime());
    }

    @Test
    void correctConcreteVehicleTypeAssigned() {
        CarRentalSystem system = new CarRentalSystem(Map.of(
                CarType.SEDAN, 1,
                CarType.SUV, 1,
                CarType.VAN, 1
        ));
        LocalDateTime now = LocalDateTime.of(2026, 6, 4, 10, 0, 0, 0);
        Reservation sedanRes = system.reserve(new ReservationRequest(CarType.SEDAN, now, 1));
        Vehicle sedanVehicle = system.getVehicleById(sedanRes.getVehicleId());
        assertTrue(sedanVehicle instanceof Sedan);
        Reservation suvRes = system.reserve(new ReservationRequest(CarType.SUV, now.plusDays(1), 1));
        Vehicle suvVehicle = system.getVehicleById(suvRes.getVehicleId());
        assertTrue(suvVehicle instanceof SUV);
        Reservation vanRes = system.reserve(new ReservationRequest(CarType.VAN, now.plusDays(2), 1));
        Vehicle vanVehicle = system.getVehicleById(vanRes.getVehicleId());
        assertTrue(vanVehicle instanceof Van);
    }

    @Test
    void polymorphismViaVehicleInterface() {
        // Even though we only expose minimal properties, we still access
        // vehicles through the Vehicle interface to demonstrate polymorphism.
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SUV, 1));
        LocalDateTime start = LocalDateTime.of(2026, 6, 4, 9, 0, 0, 0);
        Reservation res = system.reserve(new ReservationRequest(CarType.SUV, start, 1));
        Vehicle v = system.getVehicleById(res.getVehicleId());
        // We can use the Vehicle interface to obtain the car type
        assertEquals(CarType.SUV, v.getCarType());
    }
}

package com.carrental.service;

import com.carrental.exception.InvalidReservationException;
import com.carrental.exception.NoCarAvailableException;
import com.carrental.model.CarType;
import com.carrental.model.Reservation;
import com.carrental.model.ReservationRequest;
import com.carrental.vehicle.Vehicle;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CarRentalSystemTest {

    private static final LocalDateTime FIXED_START = LocalDateTime.of(2026, 6, 4, 10, 15, 30, 250_000_000);

    @Nested
    class ConstructorBehavior {

        @Test
        void givenNullVehicleCounts_whenCreatingSystem_thenThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () -> new CarRentalSystem(null));
        }

        @Test
        void givenNullVehicleCountForType_whenReservingThatType_thenNoCarAvailable() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 0));

            assertThrows(NoCarAvailableException.class,
                    () -> system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, 1)));
        }

        @Test
        void givenNegativeVehicleCountForType_whenReservingThatType_thenNoCarAvailable() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, -2));

            assertThrows(NoCarAvailableException.class,
                    () -> system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, 1)));
        }
    }

    @Nested
    class ReservationValidation {

        @Test
        void givenNullRequest_whenReserving_thenThrowsInvalidReservationException() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));

            assertThrows(InvalidReservationException.class, () -> system.reserve(null));
        }

        @Test
        void givenNullCarType_whenReserving_thenThrowsInvalidReservationException() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));

            assertThrows(InvalidReservationException.class,
                    () -> system.reserve(new ReservationRequest(null, FIXED_START, 1)));
        }

        @Test
        void givenNullStartDateTime_whenReserving_thenThrowsInvalidReservationException() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));

            assertThrows(InvalidReservationException.class,
                    () -> system.reserve(new ReservationRequest(CarType.SEDAN, null, 1)));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
        void givenInvalidDays_whenReserving_thenThrowsInvalidReservationException(int days) {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));

            assertThrows(InvalidReservationException.class,
                    () -> system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, days)));
        }
    }

    @Nested
    class ReservationBehavior {

        @Test
        void givenAvailableSedan_whenReserving_thenStoresReservationAndMetadata() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));

            Reservation reservation = system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, 2));

            assertNotNull(reservation);
            assertFalse(reservation.getReservationId().isBlank());
            assertFalse(reservation.getVehicleId().isBlank());
            assertEquals(CarType.SEDAN, reservation.getCarType());
            assertEquals(2, reservation.getNumberOfDays());
            assertEquals(FIXED_START, reservation.getStartDateTime());
            assertEquals(FIXED_START.plusDays(2), reservation.getEndDateTime());
            assertEquals(1, system.getReservationsForVehicle(reservation.getVehicleId()).size());
        }

        @Test
        void givenTwoSedans_whenOverlappingReservations_thenAssignsDifferentVehicles() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 2));

            Reservation first = system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, 2));
            Reservation second = system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START.plusHours(1), 2));

            assertNotEquals(first.getVehicleId(), second.getVehicleId());
        }

        @Test
        void givenSingleSedan_whenReservationStartsAtPreviousEnd_thenAllowed() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
            Reservation first = system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, 2));

            Reservation second = system.reserve(new ReservationRequest(CarType.SEDAN, first.getEndDateTime(), 1));

            assertNotNull(second);
        }

        @Test
        void givenSingleSedan_whenLongSequentialChainNonOverlapping_thenAllAllowed() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));

            Reservation first = system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, 1));
            Reservation second = system.reserve(new ReservationRequest(CarType.SEDAN, first.getEndDateTime(), 1));
            Reservation third = system.reserve(new ReservationRequest(CarType.SEDAN, second.getEndDateTime(), 1));

            assertNotNull(third);
            assertEquals(third.getStartDateTime(), second.getEndDateTime());
        }

        @Test
        void givenSingleSedan_whenOverlappingByOneMillisecond_thenRejected() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
            Reservation first = system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, 2));
            LocalDateTime overlappingStart = first.getEndDateTime().minus(1, ChronoUnit.MILLIS);

            assertThrows(NoCarAvailableException.class,
                    () -> system.reserve(new ReservationRequest(CarType.SEDAN, overlappingStart, 2)));
        }

        @Test
        void givenSingleSedan_whenOverlapping_thenRejected() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
            system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, 2));

            assertThrows(NoCarAvailableException.class,
                    () -> system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START.plusHours(1), 1)));
        }

        @Test
        void givenMissingTypeInInventory_whenReserving_thenRejected() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SUV, 1));

            assertThrows(NoCarAvailableException.class,
                    () -> system.reserve(new ReservationRequest(CarType.SEDAN, FIXED_START, 1)));
        }

        @Test
        void givenNanosecondPrecisionStart_whenReserving_thenTruncatesToMilliseconds() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
            LocalDateTime nanosStart = LocalDateTime.of(2026, 6, 4, 10, 15, 30, 250_123_456);

            Reservation reservation = system.reserve(new ReservationRequest(CarType.SEDAN, nanosStart, 1));

            assertEquals(250_000_000, reservation.getStartDateTime().getNano());
            assertEquals(nanosStart.truncatedTo(ChronoUnit.MILLIS).plusDays(1), reservation.getEndDateTime());
        }

        @Test
        void givenVehicleId_whenLookingUpVehicle_thenReturnsExpectedType() {
            CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SUV, 1));
            Reservation reservation = system.reserve(new ReservationRequest(CarType.SUV, FIXED_START, 1));

            Vehicle vehicle = system.getVehicleById(reservation.getVehicleId());

            assertNotNull(vehicle);
            assertEquals(CarType.SUV, vehicle.getCarType());
        }
    }
}

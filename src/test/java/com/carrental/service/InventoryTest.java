package com.carrental.service;

import com.carrental.model.CarType;
import com.carrental.model.Reservation;
import com.carrental.vehicle.Sedan;
import com.carrental.vehicle.SUV;
import com.carrental.vehicle.Vehicle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 4, 10, 0, 0, 0);

    @Test
    void givenNoVehiclesOfRequestedType_whenFindingAvailableVehicle_thenReturnsNull() {
        Inventory inventory = new Inventory();
        inventory.addVehicle(new SUV("suv-1"));

        Vehicle available = inventory.findAvailableVehicle(CarType.SEDAN, START, START.plusDays(1));

        assertNull(available);
    }

    @Test
    void givenTwoSedansAndOneBooked_whenFindingAvailableVehicle_thenReturnsUnbookedVehicle() {
        Inventory inventory = new Inventory();
        Sedan sedanOne = new Sedan("sedan-1");
        Sedan sedanTwo = new Sedan("sedan-2");
        inventory.addVehicle(sedanOne);
        inventory.addVehicle(sedanTwo);
        inventory.addReservation(reservation("res-1", sedanOne.getVehicleId(), START, START.plusDays(2)));

        Vehicle available = inventory.findAvailableVehicle(CarType.SEDAN, START.plusHours(3), START.plusDays(1));

        assertNotNull(available);
        assertEquals(sedanTwo.getVehicleId(), available.getVehicleId());
    }

    @Test
    void givenAdjacentReservationAtEndBoundary_whenFindingAvailableVehicle_thenVehicleIsAvailable() {
        Inventory inventory = new Inventory();
        Sedan sedan = new Sedan("sedan-1");
        inventory.addVehicle(sedan);
        LocalDateTime existingStart = START;
        LocalDateTime existingEnd = START.plusDays(2);
        inventory.addReservation(reservation("res-1", sedan.getVehicleId(), existingStart, existingEnd));

        Vehicle available = inventory.findAvailableVehicle(CarType.SEDAN, existingEnd, existingEnd.plusDays(1));

        assertNotNull(available);
        assertEquals(sedan.getVehicleId(), available.getVehicleId());
    }

    @Test
    void givenIdenticalRange_whenFindingAvailableVehicle_thenReturnsNull() {
        Inventory inventory = new Inventory();
        Sedan sedan = new Sedan("sedan-1");
        inventory.addVehicle(sedan);
        inventory.addReservation(reservation("res-1", sedan.getVehicleId(), START, START.plusDays(2)));

        Vehicle available = inventory.findAvailableVehicle(CarType.SEDAN, START, START.plusDays(2));

        assertNull(available);
    }

    @Test
    void givenRequestedRangeInsideExistingRange_whenFindingAvailableVehicle_thenReturnsNull() {
        Inventory inventory = new Inventory();
        Sedan sedan = new Sedan("sedan-1");
        inventory.addVehicle(sedan);
        inventory.addReservation(reservation("res-1", sedan.getVehicleId(), START, START.plusDays(5)));

        Vehicle available = inventory.findAvailableVehicle(CarType.SEDAN, START.plusDays(1), START.plusDays(2));

        assertNull(available);
    }

    @Test
    void givenExistingRangeInsideRequestedRange_whenFindingAvailableVehicle_thenReturnsNull() {
        Inventory inventory = new Inventory();
        Sedan sedan = new Sedan("sedan-1");
        inventory.addVehicle(sedan);
        inventory.addReservation(reservation("res-1", sedan.getVehicleId(), START.plusDays(1), START.plusDays(2)));

        Vehicle available = inventory.findAvailableVehicle(CarType.SEDAN, START, START.plusDays(3));

        assertNull(available);
    }

    @Test
    void givenReservationsList_whenAccessed_thenListIsUnmodifiable() {
        Inventory inventory = new Inventory();
        Sedan sedan = new Sedan("sedan-1");
        inventory.addVehicle(sedan);
        Reservation reservation = reservation("res-1", sedan.getVehicleId(), START, START.plusDays(1));
        inventory.addReservation(reservation);

        assertThrows(UnsupportedOperationException.class,
                () -> inventory.getReservationsForVehicle(sedan.getVehicleId()).add(reservation));
    }

    @Test
    void givenUnknownVehicleId_whenGettingReservations_thenReturnsEmptyUnmodifiableList() {
        Inventory inventory = new Inventory();

        assertTrue(inventory.getReservationsForVehicle("unknown").isEmpty());
        assertThrows(UnsupportedOperationException.class,
                () -> inventory.getReservationsForVehicle("unknown").add(
                        reservation("x", "unknown", START, START.plusDays(1))));
    }

    private Reservation reservation(String reservationId, String vehicleId, LocalDateTime start, LocalDateTime end) {
        return new Reservation(reservationId, vehicleId, CarType.SEDAN, start, end, 1);
    }
}

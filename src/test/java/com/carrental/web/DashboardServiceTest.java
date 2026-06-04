package com.carrental.web;

import com.carrental.model.CarType;
import com.carrental.service.CarRentalSystem;
import com.carrental.web.dto.ReservationCommand;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DashboardServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-04T10:15:30Z"), ZoneOffset.UTC);

    @Test
    void givenSystemState_whenBuildingDashboard_thenReturnsAvailabilityAndActiveReservations() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1, CarType.SUV, 1));
        system.reserve(new com.carrental.model.ReservationRequest(CarType.SEDAN,
                LocalDateTime.of(2026, 6, 4, 10, 15, 30), 1));
        DashboardService service = new DashboardService(system, CLOCK);

        var dashboard = service.getDashboard();

        assertEquals(3, dashboard.availability().size());
        assertEquals(0L, dashboard.availability().get(0).availableCount());
        assertEquals(1L, dashboard.availability().get(1).availableCount());
        assertEquals(0L, dashboard.availability().get(2).availableCount());
        assertFalse(dashboard.reservations().isEmpty());
        assertEquals("SEDAN", dashboard.reservations().get(0).carType().name());
        assertTrue(dashboard.reservations().get(0).remainingSeconds() > 0);
    }

    @Test
    void givenReservationCommand_whenCreatingReservation_thenReturnsUpdatedDashboard() {
        CarRentalSystem system = new CarRentalSystem(Map.of(CarType.SEDAN, 1));
        DashboardService service = new DashboardService(system, CLOCK);

        var response = service.createReservation(new ReservationCommand(
                CarType.SEDAN,
                LocalDateTime.of(2026, 6, 4, 10, 15, 30),
                1));

        assertTrue(response.message().contains("created successfully"));
        assertEquals(1, response.dashboard().reservations().size());
        assertEquals(0L, response.dashboard().availability().get(0).availableCount());
    }
}

package com.carrental.service;

import com.carrental.exception.InvalidReservationException;
import com.carrental.exception.NoCarAvailableException;
import com.carrental.model.CarType;
import com.carrental.model.Reservation;
import com.carrental.model.ReservationRequest;
import com.carrental.vehicle.SUV;
import com.carrental.vehicle.Sedan;
import com.carrental.vehicle.Van;
import com.carrental.vehicle.Vehicle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * CarRentalSystem orchestrates the booking process. It owns an Inventory
 * instance, validates incoming reservation requests, checks availability,
 * assigns vehicles and records reservations. Clients interact with this
 * class as the primary entry point for reservations.
 */
public class CarRentalSystem {

    private final Inventory inventory;

    /**
     * Constructs a new CarRentalSystem with the given number of vehicles
     * per car type. This constructor will create concrete vehicles
     * (Sedan, SUV, Van) and register them with the internal inventory.
     *
     * @param vehicleCounts a map of car types to the number of vehicles
     *                      available for each type
     */
    public CarRentalSystem(Map<CarType, Integer> vehicleCounts) {
        Objects.requireNonNull(vehicleCounts, "vehicleCounts must not be null");
        this.inventory = new Inventory();
        vehicleCounts.forEach((type, count) -> {
            int number = count == null ? 0 : count;
            for (int i = 0; i < number; i++) {
                String vehicleId = UUID.randomUUID().toString();
                Vehicle vehicle;
                switch (type) {
                    case SEDAN -> vehicle = new Sedan(vehicleId);
                    case SUV -> vehicle = new SUV(vehicleId);
                    case VAN -> vehicle = new Van(vehicleId);
                    default -> throw new IllegalArgumentException("Unsupported car type: " + type);
                }
                inventory.addVehicle(vehicle);
            }
        });
    }

    /**
     * Attempts to reserve a vehicle based on the provided request. The
     * request is validated and, if valid, an available vehicle of the
     * requested type is assigned. If no vehicle is available, a
     * NoCarAvailableException is thrown. If the request is invalid, an
     * InvalidReservationException is thrown.
     *
     * @param request the reservation request
     * @return a confirmed Reservation
     */
    public Reservation reserve(ReservationRequest request) {
        if (request == null) {
            throw new InvalidReservationException("Reservation request must not be null");
        }
        CarType carType = request.carType();
        LocalDateTime start = request.startDateTime();
        int days = request.numberOfDays();
        // Validate inputs
        if (carType == null) {
            throw new InvalidReservationException("Car type must not be null");
        }
        if (start == null) {
            throw new InvalidReservationException("Start date/time must not be null");
        }
        if (days <= 0) {
            throw new InvalidReservationException("Number of days must be greater than zero");
        }
        // Normalize to millisecond precision
        LocalDateTime normalizedStart = start.truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime normalizedEnd = normalizedStart.plusDays(days);
        // Check availability
        Vehicle vehicle = inventory.findAvailableVehicle(carType, normalizedStart, normalizedEnd);
        if (vehicle == null) {
            throw new NoCarAvailableException(
                    "No " + carType + " available from " + normalizedStart + " to " + normalizedEnd);
        }
        // Create reservation
        String reservationId = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(reservationId, vehicle.getVehicleId(), carType,
                normalizedStart, normalizedEnd, days);
        inventory.addReservation(reservation);
        return reservation;
    }

    /**
     * Retrieves reservations for a given vehicle. Returned list is
     * unmodifiable.
     *
     * @param vehicleId the vehicle identifier
     * @return list of reservations for the vehicle
     */
    public List<Reservation> getReservationsForVehicle(String vehicleId) {
        if (vehicleId == null) {
            return Collections.emptyList();
        }
        return inventory.getReservationsForVehicle(vehicleId);
    }

    public List<Reservation> getActiveReservations(LocalDateTime referenceTime) {
        if (referenceTime == null) {
            return Collections.emptyList();
        }
        return inventory.getAllReservations().stream()
                .filter(reservation -> reservation.getEndDateTime().isAfter(referenceTime))
                .toList();
    }

    public Map<CarType, Long> getAvailableVehicleCounts(LocalDateTime referenceTime) {
        if (referenceTime == null) {
            throw new InvalidReservationException("Reference time must not be null");
        }
        Map<CarType, Long> counts = new EnumMap<>(CarType.class);
        for (CarType carType : CarType.values()) {
            counts.put(carType, inventory.countAvailableVehicles(carType, referenceTime));
        }
        return Collections.unmodifiableMap(counts);
    }

    /**
     * Retrieves a vehicle instance by its identifier. This method is
     * provided primarily for testing to verify vehicle properties. It
     * returns {@code null} if the id is unknown.
     *
     * @param vehicleId the identifier to look up
     * @return the vehicle or {@code null}
     */
    public Vehicle getVehicleById(String vehicleId) {
        return inventory.getVehicleById(vehicleId);
    }
}
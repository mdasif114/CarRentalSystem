package com.carrental.service;

import com.carrental.model.CarType;
import com.carrental.model.Reservation;
import com.carrental.vehicle.Vehicle;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Inventory holds the collection of vehicles available for reservation and
 * tracks reservations by vehicle. It is responsible for determining
 * availability given a requested date range and car type. The class
 * encapsulates its internal state to prevent callers from accidentally
 * mutating the vehicle lists or reservation records.
 */
public class Inventory {

    private final Map<CarType, List<Vehicle>> vehiclesByType = new HashMap<>();
    private final Map<String, List<Reservation>> reservationsByVehicle = new HashMap<>();

    /**
     * Adds a new vehicle to the inventory. Vehicles are keyed by their
     * CarType to make lookup by type efficient.
     *
     * @param vehicle the vehicle to add
     */
    public void addVehicle(Vehicle vehicle) {
        vehiclesByType.computeIfAbsent(vehicle.getCarType(), k -> new ArrayList<>()).add(vehicle);
    }

    /**
     * Searches for an available vehicle of the given type that is free for
     * the provided date/time range. If no vehicle is free, {@code null}
     * is returned. The comparison uses millisecond precision: if an
     * existing reservation overlaps the requested range by even one
     * millisecond, the vehicle is considered unavailable.
     *
     * @param carType the type of vehicle desired
     * @param start   the start of the requested range (inclusive)
     * @param end     the end of the requested range (exclusive)
     * @return an available vehicle or {@code null}
     */
    public Vehicle findAvailableVehicle(CarType carType, LocalDateTime start, LocalDateTime end) {
        List<Vehicle> vehicles = vehiclesByType.getOrDefault(carType, Collections.emptyList());
        for (Vehicle vehicle : vehicles) {
            List<Reservation> reservations = reservationsByVehicle.getOrDefault(vehicle.getVehicleId(), Collections.emptyList());
            boolean available = true;
            for (Reservation reservation : reservations) {
                // Overlap condition: existingStart < requestedEnd AND requestedStart < existingEnd
                if (reservation.getStartDateTime().isBefore(end) && start.isBefore(reservation.getEndDateTime())) {
                    available = false;
                    break;
                }
            }
            if (available) {
                return vehicle;
            }
        }
        return null;
    }

    /**
     * Records a new reservation against a specific vehicle. The reservation
     * will be tracked internally to support future availability checks.
     *
     * @param reservation the reservation to add
     */
    public void addReservation(Reservation reservation) {
        reservationsByVehicle.computeIfAbsent(reservation.getVehicleId(), id -> new ArrayList<>()).add(reservation);
    }

    /**
     * Returns an unmodifiable list of reservations associated with the
     * specified vehicle. Callers cannot mutate the returned list.
     *
     * @param vehicleId the vehicle identifier
     * @return list of reservations for that vehicle
     */
    public List<Reservation> getReservationsForVehicle(String vehicleId) {
        return Collections.unmodifiableList(reservationsByVehicle.getOrDefault(vehicleId, Collections.emptyList()));
    }

    /**
     * Retrieves a vehicle by its unique identifier. This method searches
     * across all vehicle lists and returns the matching instance or
     * {@code null} if none is found. It is intended for use in tests and
     * administrative code rather than regular reservation workflows.
     *
     * @param vehicleId the identifier to search for
     * @return the vehicle if found, otherwise {@code null}
     */
    public Vehicle getVehicleById(String vehicleId) {
        for (List<Vehicle> vehicles : vehiclesByType.values()) {
            for (Vehicle v : vehicles) {
                if (v.getVehicleId().equals(vehicleId)) {
                    return v;
                }
            }
        }
        return null;
    }
}
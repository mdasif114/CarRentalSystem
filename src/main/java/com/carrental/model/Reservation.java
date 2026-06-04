package com.carrental.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a confirmed reservation in the system. This class is
 * immutable: once created, the reservation's properties cannot be
 * changed. It contains all the information required to identify a
 * booking and check for overlaps with other reservations.
 */
public class Reservation {

    private final String reservationId;
    private final String vehicleId;
    private final CarType carType;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final int numberOfDays;

    /**
     * Constructs a new Reservation.
     *
     * @param reservationId unique reservation identifier
     * @param vehicleId     identifier of the reserved vehicle
     * @param carType       type of car reserved
     * @param startDateTime start date/time (inclusive)
     * @param endDateTime   end date/time (exclusive)
     * @param numberOfDays  duration in days
     */
    public Reservation(String reservationId,
                       String vehicleId,
                       CarType carType,
                       LocalDateTime startDateTime,
                       LocalDateTime endDateTime,
                       int numberOfDays) {
        this.reservationId = Objects.requireNonNull(reservationId, "reservationId must not be null");
        this.vehicleId = Objects.requireNonNull(vehicleId, "vehicleId must not be null");
        this.carType = Objects.requireNonNull(carType, "carType must not be null");
        this.startDateTime = Objects.requireNonNull(startDateTime, "startDateTime must not be null");
        this.endDateTime = Objects.requireNonNull(endDateTime, "endDateTime must not be null");
        this.numberOfDays = numberOfDays;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public CarType getCarType() {
        return carType;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }
}
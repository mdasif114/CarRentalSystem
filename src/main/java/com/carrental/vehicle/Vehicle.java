package com.carrental.vehicle;

import com.carrental.model.CarType;

/**
 * The Vehicle interface defines the common behaviour expected from all types
 * of cars managed by the rental system. It exposes only getters to
 * emphasise encapsulation of the underlying state. Concrete implementations
 * may vary in their field values, but they all expose the same
 * behavioural contract.
 */
public interface Vehicle {

    /**
     * Unique identifier for the vehicle.
     *
     * @return a unique string identifier
     */
    String getVehicleId();

    /**
     * The type of car (Sedan, SUV or Van).
     *
     * @return the car type
     */
    CarType getCarType();

    /*
     * Additional properties could be exposed here if needed for future
     * features. However the current assessment only requires the
     * vehicle ID and car type, so the interface is kept minimal.
     */
}
package com.carrental.vehicle;

import com.carrental.model.CarType;

/**
 * Concrete implementation of a Sedan. This class extends AbstractCar and
 * provides default values for the sedan's properties. A sedan is assumed
 * to be a four-door passenger car for the purposes of this assessment.
 */
public class Sedan extends AbstractCar {

    /**
     * Creates a new Sedan with the given identifier.
     *
     * @param vehicleId unique identifier for this vehicle
     */
    public Sedan(String vehicleId) {
        super(vehicleId, CarType.SEDAN);
    }
}
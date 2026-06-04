package com.carrental.vehicle;

import com.carrental.model.CarType;

/**
 * Concrete implementation of an SUV. This class extends AbstractCar and
 * provides default values for the SUV's properties. An SUV is assumed
 * to be a sports utility vehicle for the purposes of this assessment.
 */
public class SUV extends AbstractCar {

    /**
     * Creates a new SUV with the given identifier.
     *
     * @param vehicleId unique identifier for this vehicle
     */
    public SUV(String vehicleId) {
        super(vehicleId, CarType.SUV);
    }
}
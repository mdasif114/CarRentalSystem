package com.carrental.vehicle;

import com.carrental.model.CarType;

/**
 * Concrete implementation of a Van. This class extends AbstractCar and
 * provides default values for the van's properties. A van is assumed
 * to be a larger passenger or cargo vehicle for the purposes of this assessment.
 */
public class Van extends AbstractCar {

    /**
     * Creates a new Van with the given identifier.
     *
     * @param vehicleId unique identifier for this vehicle
     */
    public Van(String vehicleId) {
        super(vehicleId, CarType.VAN);
    }
}
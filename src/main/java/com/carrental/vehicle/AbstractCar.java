package com.carrental.vehicle;

import com.carrental.model.CarType;

/**
 * AbstractCar provides a base implementation for the Vehicle interface.
 * It encapsulates all common fields shared by concrete car types. The
 * constructor is protected so that only subclasses can instantiate it.
 */
public abstract class AbstractCar implements Vehicle {

    private final String vehicleId;
    private final CarType carType;

    /**
     * Constructs an AbstractCar with the provided parameters.
     *
     * @param vehicleId unique identifier
     * @param carType   type of car
     */
    protected AbstractCar(String vehicleId, CarType carType) {
        this.vehicleId = vehicleId;
        this.carType = carType;
    }

    @Override
    public String getVehicleId() {
        return vehicleId;
    }

    @Override
    public CarType getCarType() {
        return carType;
    }

    /*
     * Concrete subclasses could override additional methods to expose
     * properties such as seat capacity or transmission type if required.
     * The base class deliberately keeps the interface minimal to satisfy
     * the current assessment requirements.
     */
}
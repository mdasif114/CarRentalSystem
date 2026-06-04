package com.carrental.model;

import com.carrental.vehicle.SUV;
import com.carrental.vehicle.Sedan;
import com.carrental.vehicle.Van;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VehicleModelTest {

    @Test
    void givenSedan_whenCreated_thenHasSedanTypeAndVehicleId() {
        Sedan sedan = new Sedan("sedan-123");

        assertEquals("sedan-123", sedan.getVehicleId());
        assertEquals(CarType.SEDAN, sedan.getCarType());
    }

    @Test
    void givenSuv_whenCreated_thenHasSuvTypeAndVehicleId() {
        SUV suv = new SUV("suv-123");

        assertEquals("suv-123", suv.getVehicleId());
        assertEquals(CarType.SUV, suv.getCarType());
    }

    @Test
    void givenVan_whenCreated_thenHasVanTypeAndVehicleId() {
        Van van = new Van("van-123");

        assertEquals("van-123", van.getVehicleId());
        assertEquals(CarType.VAN, van.getCarType());
    }
}

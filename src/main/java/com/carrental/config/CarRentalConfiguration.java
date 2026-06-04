package com.carrental.config;

import com.carrental.model.CarType;
import com.carrental.service.CarRentalSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.Map;

@Configuration
public class CarRentalConfiguration {

    @Bean
    public CarRentalSystem carRentalSystem() {
        return new CarRentalSystem(Map.of(
                CarType.SEDAN, 2,
                CarType.SUV, 1,
                CarType.VAN, 1
        ));
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}

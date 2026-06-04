package com.carrental.web.dto;

import com.carrental.model.CarType;

import java.time.LocalDateTime;

public record ReservationCommand(CarType carType, LocalDateTime startDateTime, int numberOfDays) {
}

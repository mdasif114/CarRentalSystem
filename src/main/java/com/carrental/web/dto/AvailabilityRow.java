package com.carrental.web.dto;

import com.carrental.model.CarType;

public record AvailabilityRow(CarType carType, long availableCount) {
}

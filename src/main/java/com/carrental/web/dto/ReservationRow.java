package com.carrental.web.dto;

import com.carrental.model.CarType;

public record ReservationRow(
        String reservationId,
        String vehicleId,
        CarType carType,
        String startDateTime,
        String endDateTime,
        long remainingSeconds,
        String remainingLabel) {
}

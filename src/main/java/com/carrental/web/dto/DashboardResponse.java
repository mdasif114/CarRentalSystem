package com.carrental.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DashboardResponse(
        LocalDateTime generatedAt,
        List<AvailabilityRow> availability,
        List<ReservationRow> reservations) {
}

package com.carrental.web;

import com.carrental.model.CarType;
import com.carrental.model.Reservation;
import com.carrental.model.ReservationRequest;
import com.carrental.service.CarRentalSystem;
import com.carrental.web.dto.AvailabilityRow;
import com.carrental.web.dto.DashboardResponse;
import com.carrental.web.dto.ReservationCommand;
import com.carrental.web.dto.ReservationResponse;
import com.carrental.web.dto.ReservationRow;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DashboardService {

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CarRentalSystem carRentalSystem;
    private final Clock clock;

    public DashboardService(CarRentalSystem carRentalSystem, Clock clock) {
        this.carRentalSystem = carRentalSystem;
        this.clock = clock;
    }

    public DashboardResponse getDashboard() {
        return buildDashboard(LocalDateTime.now(clock));
    }

    public ReservationResponse createReservation(ReservationCommand command) {
        Objects.requireNonNull(command, "Reservation command must not be null");
        Reservation reservation = carRentalSystem.reserve(
                new ReservationRequest(command.carType(), command.startDateTime(), command.numberOfDays()));
        String message = "Reservation " + reservation.getReservationId() + " created successfully";
        return new ReservationResponse(message, buildDashboard(LocalDateTime.now(clock)));
    }

    private DashboardResponse buildDashboard(LocalDateTime now) {
        Map<CarType, Long> availability = carRentalSystem.getAvailableVehicleCounts(now);
        List<AvailabilityRow> availabilityRows = new ArrayList<>();
        for (CarType carType : CarType.values()) {
            availabilityRows.add(new AvailabilityRow(carType, availability.getOrDefault(carType, 0L)));
        }

        List<ReservationRow> reservationRows = carRentalSystem.getActiveReservations(now).stream()
                .sorted(Comparator.comparing(Reservation::getEndDateTime).thenComparing(Reservation::getReservationId))
                .map(reservation -> toReservationRow(reservation, now))
                .toList();
        return new DashboardResponse(now, List.copyOf(availabilityRows), reservationRows);
    }

    private ReservationRow toReservationRow(Reservation reservation, LocalDateTime now) {
        Duration remaining = Duration.between(now, reservation.getEndDateTime());
        long remainingSeconds = Math.max(0, remaining.toSeconds());
        return new ReservationRow(
                reservation.getReservationId(),
                reservation.getVehicleId(),
                reservation.getCarType(),
                reservation.getStartDateTime().format(DISPLAY_FORMATTER),
                reservation.getEndDateTime().format(DISPLAY_FORMATTER),
                remainingSeconds,
                formatRemaining(remainingSeconds));
    }

    private String formatRemaining(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}

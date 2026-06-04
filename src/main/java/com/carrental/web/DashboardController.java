package com.carrental.web;

import com.carrental.web.dto.DashboardResponse;
import com.carrental.web.dto.ReservationCommand;
import com.carrental.web.dto.ReservationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboardPage() {
        return "dashboard";
    }

    @GetMapping("/api/dashboard")
    @ResponseBody
    public DashboardResponse dashboard() {
        return dashboardService.getDashboard();
    }

    @PostMapping("/api/reservations")
    @ResponseBody
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationCommand command) {
        ReservationResponse response = dashboardService.createReservation(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

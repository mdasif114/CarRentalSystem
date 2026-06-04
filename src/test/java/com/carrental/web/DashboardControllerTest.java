package com.carrental.web;

import com.carrental.exception.NoCarAvailableException;
import com.carrental.model.CarType;
import com.carrental.web.dto.AvailabilityRow;
import com.carrental.web.dto.DashboardResponse;
import com.carrental.web.dto.ReservationCommand;
import com.carrental.web.dto.ReservationResponse;
import com.carrental.web.dto.ReservationRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import(GlobalExceptionHandler.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void givenDashboardRequest_whenLoadingPage_thenReturnsView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    @Test
    void givenDashboardRequest_whenLoadingApi_thenReturnsJsonPayload() throws Exception {
        when(dashboardService.getDashboard()).thenReturn(new DashboardResponse(
                LocalDateTime.of(2026, 6, 4, 10, 15, 30),
                List.of(new AvailabilityRow(CarType.SEDAN, 1)),
                List.of(new ReservationRow("res-1", "veh-1", CarType.SEDAN, "2026-06-04 10:15:30",
                        "2026-06-05 10:15:30", 3600, "01:00:00"))));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availability[0].carType").value("SEDAN"))
                .andExpect(jsonPath("$.reservations[0].reservationId").value("res-1"));
    }

    @Test
    void givenValidReservation_whenPosting_thenReturnsCreatedResponse() throws Exception {
        when(dashboardService.createReservation(any(ReservationCommand.class))).thenReturn(
                new ReservationResponse("Reservation created successfully",
                        new DashboardResponse(LocalDateTime.now(),
                                List.of(new AvailabilityRow(CarType.SEDAN, 0)),
                                List.of())));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"carType":"SEDAN","startDateTime":"2026-06-04T10:15:30","numberOfDays":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Reservation created successfully"));
    }

    @Test
    void givenNoAvailability_whenPosting_thenReturnsConflict() throws Exception {
        when(dashboardService.createReservation(any(ReservationCommand.class)))
                .thenThrow(new NoCarAvailableException("No Sedan available"));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"carType":"SEDAN","startDateTime":"2026-06-04T10:15:30","numberOfDays":1}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("No Sedan available"));
    }
}

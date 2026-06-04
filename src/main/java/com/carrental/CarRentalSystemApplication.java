package com.carrental;

import com.carrental.exception.InvalidReservationException;
import com.carrental.exception.NoCarAvailableException;
import com.carrental.model.CarType;
import com.carrental.model.Reservation;
import com.carrental.model.ReservationRequest;
import com.carrental.service.CarRentalSystem;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class CarRentalSystemApplication {

    private static final List<DateTimeFormatter> INPUT_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    );

    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        //SpringApplication.run(CarRentalSystemApplication.class, args);
        Map<CarType, Integer> vehicleCounts = Map.of(
                CarType.SEDAN, 2,
                CarType.SUV, 1,
                CarType.VAN, 1
        );
        CarRentalSystem system = new CarRentalSystem(vehicleCounts);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to the Car Rental demo!");
            System.out.println("Inventory: 2 Sedans, 1 SUV, 1 Van");
            System.out.println("Available car types: SEDAN, SUV, VAN");
            System.out.println("Type EXIT at the car type prompt to close the demo.");

            while (true) {
                System.out.println();
                System.out.print("Enter car type to reserve (SEDAN/SUV/VAN) or EXIT: ");
                String typeInput = scanner.nextLine().trim().toUpperCase();

                if ("EXIT".equals(typeInput)) {
                    System.out.println("Exiting Car Rental demo. Goodbye!");
                    break;
                }

                CarType carType;
                try {
                    carType = CarType.valueOf(typeInput);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Invalid car type. Please enter SEDAN, SUV, VAN or EXIT.");
                    continue;
                }

                System.out.print("Enter start date/time (examples: 2026-06-04 01:45 or 2026-06-04T01:45:00.250): ");
                String dateTimeInput = scanner.nextLine().trim();
                LocalDateTime start;
                try {
                    start = parseDateTime(dateTimeInput);
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                    continue;
                }

                System.out.print("Enter number of days: ");
                int numberOfDays;
                try {
                    numberOfDays = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid number of days. Please enter a positive integer.");
                    continue;
                }

                try {
                    Reservation reservation = system.reserve(new ReservationRequest(carType, start, numberOfDays));
                    printSuccessfulReservation(reservation);
                } catch (InvalidReservationException | NoCarAvailableException ex) {
                    System.out.println("Could not create reservation: " + ex.getMessage());
                }
            }
        }
    }

    private static LocalDateTime parseDateTime(String input) {
        for (DateTimeFormatter formatter : INPUT_FORMATTERS) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (Exception ignored) {
                // Try the next supported format.
            }
        }
        throw new IllegalArgumentException(
                "Invalid date/time format. Use one of: yyyy-MM-dd HH:mm, yyyy-MM-dd HH:mm:ss.SSS, "
                        + "yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    private static void printSuccessfulReservation(Reservation reservation) {
        System.out.println("Reservation successful!");
        System.out.printf("Reservation ID: %s%n", reservation.getReservationId());
        System.out.printf("Vehicle ID: %s%n", reservation.getVehicleId());
        System.out.printf("Car type: %s%n", reservation.getCarType());
        System.out.printf("Start: %s%n", reservation.getStartDateTime().format(OUTPUT_FORMATTER));
        System.out.printf("End: %s%n", reservation.getEndDateTime().format(OUTPUT_FORMATTER));
    }

}

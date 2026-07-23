# Car Rental System

A Spring Boot application that models vehicle inventory, date-range availability, reservations, and a small web dashboard.

The project demonstrates object-oriented Java design, domain validation, reservation-overlap detection, exception handling, MVC web integration, and focused automated tests.

## Capabilities

- Model sedans, SUVs, vans, reservations, and reservation requests
- Allocate an available vehicle for a requested date range
- Reject invalid reservation requests
- Prevent overlapping reservations for the same vehicle
- Expose dashboard and reservation endpoints
- Return read-only views of reservation and availability data
- Run the application and tests with Maven Wrapper

## Technology

- Java 21
- Spring Boot 4
- Spring MVC
- Thymeleaf
- Maven
- JUnit 5

## Project structure

~~~text
src/main/java/com/carrental/
├── config/       application wiring
├── exception/    domain exceptions
├── model/        reservation and request records
├── service/      inventory and reservation orchestration
├── vehicle/      vehicle hierarchy
└── web/          MVC controller, dashboard service, and DTOs

src/test/java/com/carrental/
├── model/
├── service/
├── exception/
└── web/
~~~

## Run locally

Prerequisites:

- JDK 21
- Bash, or a shell that can run Maven Wrapper

Start the application:

~~~bash
./mvnw spring-boot:run
~~~

Open the dashboard at http://localhost:8080.

Run the test suite:

~~~bash
./mvnw test
~~~

## HTTP API

| Method | Endpoint | Purpose |
|---|---|---|
| GET | / | Render the dashboard |
| GET | /api/dashboard | Return dashboard availability and reservation data |
| POST | /api/reservations | Create a reservation |

The reservation endpoint accepts the JSON shape represented by the ReservationCommand DTO. See the DTOs and controller tests for the current contract.

## Design notes

The core booking path is deliberately small:

1. Validate the request.
2. Normalize the requested start time.
3. Calculate the exclusive end time.
4. Find a compatible vehicle with no overlapping reservation.
5. Record and return the reservation.

The current implementation is an in-memory reference application. Persistence, authentication, cancellation, pricing, concurrency control, and distributed deployment are intentionally outside the current scope.

See:

- Architecture: docs/architecture.md
- Failure modes: docs/failure-modes.md

## Current limitations and next steps

- Inventory and reservations are process-local and are lost on restart.
- Reservation allocation is not yet protected by a database constraint or distributed lock.
- There is no authentication or authorization layer.
- Pricing, cancellation, payment, and notification workflows are not implemented.
- Integration tests do not yet exercise external persistence or concurrent requests.

These limitations are documented so the repository distinguishes implemented behavior from planned engineering work.

## CI

Every push and pull request to main runs the Maven verification workflow using Java 21.

# Architecture

## Context

The application represents a small car-rental booking service. A caller selects a vehicle type and date range. The system validates the request, finds a compatible vehicle, records the reservation, and exposes the result through a web dashboard and JSON endpoints.

## Component view

~~~mermaid
flowchart TD
    Client[Browser or API client] --> Web[DashboardController]
    Web --> Dashboard[DashboardService]
    Dashboard --> Booking[CarRentalSystem]
    Booking --> Inventory[Inventory]
    Inventory --> Vehicles[Vehicle collections]
    Inventory --> Reservations[Reservation records]
~~~

## Responsibilities

| Component | Responsibility |
|---|---|
| DashboardController | Maps browser and HTTP requests to application operations |
| DashboardService | Adapts web DTOs to domain requests and responses |
| CarRentalSystem | Validates booking requests and orchestrates reservation creation |
| Inventory | Stores vehicles/reservations and evaluates availability |
| Vehicle hierarchy | Encapsulates vehicle type and identity |
| Reservation | Represents a confirmed time-bounded booking |

## Reservation flow

~~~mermaid
sequenceDiagram
    participant C as Client
    participant W as Web layer
    participant B as Booking service
    participant I as Inventory

    C->>W: POST /api/reservations
    W->>B: ReservationCommand
    B->>B: Validate and normalize dates
    B->>I: Find available vehicle
    I-->>B: Vehicle or no match
    B->>I: Store reservation
    B-->>W: ReservationResponse
    W-->>C: 201 Created
~~~

## Availability rule

An existing reservation overlaps a requested interval when:

~~~text
existingStart < requestedEnd
AND
requestedStart < existingEnd
~~~

The end of a reservation is exclusive. Therefore, a vehicle can be booked again exactly when the previous reservation ends.

## State model

The current implementation keeps state in memory:

- vehicles are grouped by CarType;
- reservations are indexed by vehicle ID;
- reads return immutable list/map views;
- a process restart clears all state.

This is appropriate for a focused domain exercise, but not for a production booking service.

## Evolution path

A production-oriented version would introduce:

1. a persistent reservation store with a uniqueness/overlap strategy;
2. transactional allocation;
3. optimistic or pessimistic concurrency control;
4. authentication and authorization;
5. pricing and payment boundaries;
6. idempotency keys for retried booking requests;
7. metrics, tracing, structured logs, and alerting.

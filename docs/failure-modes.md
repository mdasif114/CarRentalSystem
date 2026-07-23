# Failure Modes

This document distinguishes current behavior from production hardening that is not yet implemented.

| Failure mode | Current behavior | Production consideration |
|---|---|---|
| Null or invalid request | Throws InvalidReservationException | Return a stable validation response with field-level errors |
| No compatible vehicle | Throws NoCarAvailableException | Return a conflict/availability response and expose a useful metric |
| Overlapping reservation | Vehicle is excluded by the interval rule | Enforce the invariant transactionally in persistent storage |
| Process restart | All in-memory state is lost | Persist vehicles and reservations |
| Two concurrent bookings | No distributed coordination exists | Use a database transaction, locking or a reservation command boundary |
| Client retry | A second request may create another reservation | Require an idempotency key and store request outcomes |
| Downstream payment failure | Payment is outside the current scope | Model reservation/payment state and compensating actions |
| Unhandled web exception | Framework-level error handling applies | Return a versioned error contract and correlation ID |
| Invalid or extreme date input | Basic domain validation is applied | Define bounds, clock policy and timezone rules explicitly |

## Observability plan

A production implementation should record:

- reservation request count and success/failure rate;
- no-vehicle conflicts by vehicle type;
- booking latency;
- idempotency replay count;
- persistence/concurrency conflicts;
- dependency failures;
- correlation IDs across the request flow.

No production monitoring is claimed by this repository yet.

package com.carrental.model;

import java.time.LocalDateTime;

/**
 * ReservationRequest is a Java record used to collect the minimal set of
 * information required from a caller in order to attempt a booking. Using
 * a record here makes the intent clear and reduces boilerplate for simple
 * immutable data carriers. The CarRentalSystem validates the contents of
 * this request.
 *
 * @param carType       the requested type of car
 * @param startDateTime the desired start date and time
 * @param numberOfDays  the number of days to reserve for
 */
public record ReservationRequest(CarType carType, LocalDateTime startDateTime, int numberOfDays) {
}
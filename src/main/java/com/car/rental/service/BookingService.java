package com.car.rental.service;

import com.car.rental.model.Reservation;
import com.car.rental.model.User;
import com.car.rental.model.car.Car; // <-- Import
import com.car.rental.model.car.enums.CarType;

import java.time.ZonedDateTime;
import java.util.List; // <-- Import
import java.util.Optional;

public interface BookingService {

    List<Car> findAvailableCars(CarType type, ZonedDateTime startDate, int durationInDays);

    Optional<Reservation> reserve(User user, String licensePlate, ZonedDateTime startDate, int durationInDays);

    boolean cancelReservation(User user, String reservationId);

}
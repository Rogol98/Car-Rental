package com.car.rental.service.impl;

import com.car.rental.model.Reservation;
import com.car.rental.model.User;
import com.car.rental.model.car.Car;
import com.car.rental.model.car.enums.CarType;
import com.car.rental.service.BookingService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingServiceImpl implements BookingService {

    private final List<Car> fleet;
    private final List<Reservation> reservations;

    public BookingServiceImpl(List<Car> fleet) {
        this.fleet = new ArrayList<>(fleet);
        this.reservations = new ArrayList<>();
    }

    @Override
    public Optional<Reservation> reserve(User user, String licensePlate, ZonedDateTime startDate, int durationInDays) {
        if (durationInDays <= 0) {
            return Optional.empty();
        }

        Optional<Car> carToBook = fleet.stream()
                .filter(car -> car.getLicensePlate().equals(licensePlate))
                .findFirst();

        if (carToBook.isEmpty()) {
            return Optional.empty();
        }

        ZonedDateTime endDate = startDate.plusDays(durationInDays);

        boolean isAlreadyBooked = reservations.stream()
                .filter(res -> res.getCar().getLicensePlate().equals(licensePlate))
                .anyMatch(res -> res.overlaps(startDate, endDate));

        if (isAlreadyBooked) {
            return Optional.empty();
        }

        Reservation newReservation = new Reservation(carToBook.get(), user, startDate, durationInDays);
        this.reservations.add(newReservation);
        return Optional.of(newReservation);
    }

    @Override
    public boolean cancelReservation(User user, String reservationId) {
        Optional<Reservation> reservationToCancel = this.reservations.stream()
                .filter(res -> res.getReservationId().equals(reservationId))
                .findFirst();

        if (reservationToCancel.isEmpty()) {
            return false;
        }

        Reservation res = reservationToCancel.get();

        if (!res.getUser().equals(user)) {
            return false;
        }

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startTime = res.getStartDate();
        ZonedDateTime cancellationDeadline = startTime.minusHours(24);

        if (now.isBefore(cancellationDeadline)) {
            this.reservations.remove(res);
            return true;
        }

        return false;
    }

    @Override
    public List<Car> findAvailableCars(CarType type, ZonedDateTime startDate, int durationInDays) {
        ZonedDateTime endDate = startDate.plusDays(durationInDays);

        List<Car> carsOfType = fleet.stream()
                .filter(car -> car.getCarType() == type)
                .toList();

        List<Reservation> conflictingReservations = reservations.stream()
                .filter(res -> res.overlaps(startDate, endDate))
                .toList();

        List<Car> bookedCars = conflictingReservations.stream()
                .map(Reservation::getCar)
                .toList();

        return carsOfType.stream()
                .filter(car -> !bookedCars.contains(car))
                .collect(Collectors.toList());
    }

}

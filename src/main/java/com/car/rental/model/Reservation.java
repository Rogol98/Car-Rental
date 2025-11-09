package com.car.rental.model;

import com.car.rental.model.car.Car;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public class Reservation {

    private final String reservationId;
    private final Car car;
    private final User user;
    private final ZonedDateTime startDate;
    private final ZonedDateTime endDate;
    private final int durationInDays;

    public Reservation(Car car, User user, ZonedDateTime startDate, int durationInDays) {
        this.reservationId = UUID.randomUUID().toString();
        this.car = car;
        this.user = user;
        this.startDate = startDate;
        this.durationInDays = durationInDays;
        this.endDate = startDate.plusDays(durationInDays);
    }

    public String getReservationId() {
        return reservationId;
    }

    public Car getCar() {
        return car;
    }

    public User getUser() {
        return user;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public boolean overlaps(ZonedDateTime newStart, ZonedDateTime newEnd) {
        return newStart.isBefore(this.endDate) && newEnd.isAfter(this.startDate);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return durationInDays == that.durationInDays && Objects.equals(reservationId, that.reservationId) && Objects.equals(car, that.car) && Objects.equals(user, that.user) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, car, user, startDate, endDate, durationInDays);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", car=" + car +
                ", user=" + user +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", durationInDays=" + durationInDays +
                '}';
    }

}

package com.car.rental.service;

import com.car.rental.model.Reservation;
import com.car.rental.model.User;
import com.car.rental.model.car.Car;
import com.car.rental.model.car.Sedan;
import com.car.rental.model.car.Suv;
import com.car.rental.model.car.enums.CarType;
import com.car.rental.model.car.enums.FuelType;
import com.car.rental.model.car.enums.TransmissionType;
import com.car.rental.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceImplTest {

    private BookingService bookingService;
    private User testUser1;
    private User testUser2;
    private ZonedDateTime testDate;

    private Car sedan1;
    private Car sedan2;
    private Car suv1;

    @BeforeEach
    void setUp() {
        sedan1 = new Sedan(FuelType.GASOLINE, TransmissionType.AUTOMATIC, 50000, 150, "S-001");
        sedan2 = new Sedan(FuelType.DIESEL, TransmissionType.MANUAL, 80000, 130, "S-002");
        suv1 = new Suv(FuelType.HYBRID, TransmissionType.AUTOMATIC, 20000, 210, "V-001");

        List<Car> fleet = new ArrayList<>(List.of(sedan1, sedan2, suv1));

        bookingService = new BookingServiceImpl(fleet);

        testUser1 = new User("John", "Doe", "john@test.com", "123456789");
        testUser2 = new User("Jane", "Smith", "jane@test.com", "987654321");

        testDate = ZonedDateTime.of(2025, 12, 1, 10, 0, 0, 0, ZoneId.of("Europe/Warsaw"));
    }

    @Test
    void findAvailableCarsSuccessTest() {
        List<Car> availableCars = bookingService.findAvailableCars(CarType.SEDAN, testDate, 3);

        // Should find 2 sedans
        assertNotNull(availableCars);
        assertEquals(2, availableCars.size());
        assertTrue(availableCars.contains(sedan1) && availableCars.contains(sedan2));
    }

    @Test
    void findAvailableSuv() {
        List<Car> availableSuvs = bookingService.findAvailableCars(CarType.SUV, testDate, 3);

        assertEquals(1, availableSuvs.size(), "Should find exactly one SUV");

        assertEquals(suv1, availableSuvs.getFirst());
    }

    @Test
    void findAvailableCarsFailsWhenNoCarsOfTypeTest() {
        // Search for Vans (we have 0)
        List<Car> availableCars = bookingService.findAvailableCars(CarType.VAN, testDate, 3);

        assertNotNull(availableCars);
        assertTrue(availableCars.isEmpty(), "Should find no Vans");
    }

    @Test
    void findAvailableCarsFailsWhenAllCarsAreBookedTest() {
        // Arrange: Book both sedans
        bookingService.reserve(testUser1, "S-001", testDate, 3);
        bookingService.reserve(testUser2, "S-002", testDate, 3);

        // Search for Sedans at the same date
        List<Car> availableCars = bookingService.findAvailableCars(CarType.SEDAN, testDate, 3);

        // Should find 0
        assertNotNull(availableCars);
        assertTrue(availableCars.isEmpty(), "Should find no available Sedans");
    }

    @Test
    void successfulReservationTest() {
        Optional<Reservation> optionalReservation = bookingService.reserve(testUser1, "S-001", testDate, 3);

        assertTrue(optionalReservation.isPresent(), "Reservation should be successful");
        assertEquals("S-001", optionalReservation.get().getCar().getLicensePlate());
        assertEquals(testUser1, optionalReservation.get().getUser());
    }

    @Test
    void reservationFailsForNonExistentLicensePlateTest() {
        Optional<Reservation> result = bookingService.reserve(testUser1, "NON-EXISTENT", testDate, 3);
        assertFalse(result.isPresent(), "Reservation should fail for a car that doesn't exist");
    }

    @Test
    void reservationFailsWhenSpecificCarIsBookedTest() {
        bookingService.reserve(testUser1, "V-001", testDate, 3);

        // Try to book the same SUV in an overlapping period
        Optional<Reservation> result = bookingService.reserve(testUser2, "V-001", testDate.plusDays(1), 3);

        assertFalse(result.isPresent(), "Reservation should fail as 'V-001' is booked");
    }

    @Test
    void bookDifferentCarsAtSameTimeHoursTest() {
        assertTrue(bookingService.reserve(testUser1, "S-001", testDate, 3).isPresent());
        assertTrue(bookingService.reserve(testUser2, "S-002", testDate, 3).isPresent());

        // Try to book an already booked car
        Optional<Reservation> result1 = bookingService.reserve(testUser1, "S-001", testDate.minusHours(8), 1);
        Optional<Reservation> result2 = bookingService.reserve(testUser1, "S-002", testDate.minusHours(23), 1);

        assertFalse(result1.isPresent(), "Reservation should fail, 'S-001' is already booked");
        assertFalse(result2.isPresent(), "Reservation should fail, 'S-002' is already booked");
    }

    @Test
    void bookSameCarAtNonOverlappingTimesTest() {
        assertTrue(bookingService.reserve(testUser1, "V-001", testDate, 3).isPresent());

        // Book the same SUV after the first reservation ends
        ZonedDateTime laterDate = testDate.plusDays(3); // The exact end date
        Optional<Reservation> result = bookingService.reserve(testUser2, "V-001", laterDate, 2);

        assertTrue(result.isPresent(), "Should be able to book 'V-001' after the first reservation ends");
    }

    @Test
    void successfulCancellationTest() {
        Optional<Reservation> reservation = bookingService.reserve(testUser1, "S-001", testDate.plusDays(2), 3);
        assertTrue(reservation.isPresent());
        String reservationId = reservation.get().getReservationId();

        boolean result = bookingService.cancelReservation(testUser1, reservationId);

        assertTrue(result, "Cancellation should be successful");
    }

    @Test
    void cancellationFailsIfTooLateTest() {
        ZonedDateTime soonDate = ZonedDateTime.now(ZoneId.of("Europe/Warsaw")).plusHours(12);
        Optional<Reservation> reservation = bookingService.reserve(testUser1, "S-001", soonDate, 3);

        assertTrue(reservation.isPresent());
        String reservationId = reservation.get().getReservationId();

        boolean result = bookingService.cancelReservation(testUser1, reservationId);

        assertFalse(result, "Cancellation should fail as it's less than 24 hours before start");
    }

    @Test
    void cancellationFailsForWrongUserTest() {
        Optional<Reservation> reservation = bookingService.reserve(testUser1, "S-001", testDate.plusDays(30), 3);
        assertTrue(reservation.isPresent());
        String reservationId = reservation.get().getReservationId();

        boolean result = bookingService.cancelReservation(testUser2, reservationId);

        assertFalse(result, "Cancellation should fail as User 2 does not own the reservation");
    }

    @Test
    void reservationFailsForZeroDurationTest() {
        Optional<Reservation> result = bookingService.reserve(testUser1, "S-001", testDate, 0);
        assertFalse(result.isPresent(), "Reservation for zero days should not be allowed");
    }

    @Test
    void reservationFailsForNegativeDurationTest() {
        Optional<Reservation> result = bookingService.reserve(testUser1, "S-001", testDate, -1);
        assertFalse(result.isPresent(), "Reservation for negative days should not be allowed");
    }

    @Test
    void cancellationMakesCarAvailableAgainTest() {
        Optional<Reservation> firstBooking = bookingService.reserve(testUser1, "S-001", testDate, 5);
        assertTrue(firstBooking.isPresent(), "Step 1: Initial booking of 'S-001' should be successful");
        String reservationId = firstBooking.get().getReservationId();

        // Fail to rebook
        Optional<Reservation> secondBookingAttempt = bookingService.reserve(testUser2, "S-001", testDate, 5);
        assertFalse(secondBookingAttempt.isPresent(), "Step 2: 'S-001' should be unavailable as it's already booked");

        // testUser1 cancels the original booking
        boolean cancelResult = bookingService.cancelReservation(testUser1, reservationId);
        assertTrue(cancelResult, "Step 3: Cancellation by testUser1 should be successful");

        // Proof the car is available again
        Optional<Reservation> thirdBookingAttempt = bookingService.reserve(testUser2, "S-001", testDate, 5);
        assertTrue(thirdBookingAttempt.isPresent(), "Step 4: 'S-001' should be available again after cancellation");
        assertEquals(testUser2, thirdBookingAttempt.get().getUser(), "The new booking should be owned by testUser2");
    }

}
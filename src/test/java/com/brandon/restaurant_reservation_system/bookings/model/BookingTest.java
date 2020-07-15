package com.brandon.restaurant_reservation_system.bookings.model;

import com.brandon.restaurant_reservation_system.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Booking booking;
    private final User user = new User();

    @BeforeEach
    void setUp() {
        booking = new Booking(
                2,
                LocalDateTime.of(2020, 10, 10, 20, 0),
                LocalDateTime.of(2020, 10, 10, 22, 0),
                user
        );
    }

    @Test
    void updateBooking() {
        Booking newBooking = new Booking(
                4,
                LocalDateTime.of(2020, 9, 10, 20, 0),
                LocalDateTime.of(2020, 9, 10, 22, 0),
                user
        );

        booking.updateBooking(newBooking);
        assertEquals(newBooking.getStartTime(), booking.getStartTime());
        assertEquals(newBooking.getEndTime(), booking.getEndTime());
        assertEquals(newBooking.getPartySize(), booking.getPartySize());
    }

    @Test
    void doTheseBookingsOverlap() {
        Booking bookingDoesOverlap = new Booking(
                4,
                LocalDateTime.of(2020, 10, 10, 20, 0),
                LocalDateTime.of(2020, 10, 10, 22, 0),
                user
        );

        Booking bookingDoesOverlap2 = new Booking(
                4,
                LocalDateTime.of(2020, 10, 10, 21, 0),
                LocalDateTime.of(2020, 10, 10, 23, 0),
                user
        );

        Booking bookingDoesOverlap3 = new Booking(
                4,
                LocalDateTime.of(2020, 10, 10, 19, 0),
                LocalDateTime.of(2020, 10, 10, 21, 0),
                user
        );
        Booking bookingDoesNotOverlap = new Booking(
                4,
                LocalDateTime.of(2020, 10, 10, 15, 0),
                LocalDateTime.of(2020, 10, 10, 17, 0),
                user
        );
        Booking bookingDoesNotOverlap2 = new Booking(
                4,
                LocalDateTime.of(2020, 10, 10, 18, 0),
                LocalDateTime.of(2020, 10, 10, 20, 0),
                user
        );

        Booking bookingDoesNotOverlap3 = new Booking(
                4,
                LocalDateTime.of(2020, 10, 9, 15, 0),
                LocalDateTime.of(2020, 10, 9, 17, 0),
                user
        );

        assertTrue(booking.doTheseBookingsOverlap(bookingDoesOverlap));
        assertTrue(booking.doTheseBookingsOverlap(bookingDoesOverlap2));
        assertTrue(booking.doTheseBookingsOverlap(bookingDoesOverlap3));
        System.out.println("********************************************");
        System.out.println("\n \n \n \n");
        System.out.println(booking.getStartTime());
        System.out.println(booking.getEndTime());
        System.out.println(bookingDoesNotOverlap2.getStartTime());
        System.out.println(bookingDoesNotOverlap2.getEndTime());

        System.out.println("\n \n \n \n");
        System.out.println("********************************************");
        assertFalse(booking.doTheseBookingsOverlap(bookingDoesNotOverlap));
        assertFalse(booking.doTheseBookingsOverlap(bookingDoesNotOverlap2));
        assertFalse(booking.doTheseBookingsOverlap(bookingDoesNotOverlap3));
    }

    @Test
    void isTheBookingDuringThisTime() {
        boolean result = booking.isTheBookingDuringThisTime(
                LocalDateTime.of(2020, 10, 10, 20, 0),
                LocalDateTime.of(2020, 10, 10, 22, 0));
        boolean result2 = booking.isTheBookingDuringThisTime(
                LocalDateTime.of(2020, 10, 9, 20, 0),
                LocalDateTime.of(2020, 10, 9, 22, 0));

        assertTrue(result);
        assertFalse(result2);

    }


    @Test
    void equals() {
        Booking equalBooking = new Booking(
                2,
                LocalDateTime.of(2020, 10, 10, 20, 0),
                LocalDateTime.of(2020, 10, 10, 22, 0),
                user
        );

        assertEquals(booking, booking);
        assertEquals(booking, equalBooking);
        assertNotEquals(booking, new Booking());
    }
}
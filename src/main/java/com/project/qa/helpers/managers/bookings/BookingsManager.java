package com.project.qa.helpers.managers.bookings;

import com.project.qa.api.controller.managers.BookingsControllerApi;
import com.project.qa.helpers.exceptions.InstanceAlreadyPickedException;
import com.project.qa.helpers.models.bookings.Booking;
import com.project.qa.helpers.models.bookings.BookingData;
import com.project.qa.helpers.user.engine.UserSession;
import com.project.qa.helpers.utils.RandomUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.UndeclaredThrowableException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Component
public class BookingsManager {
    @Autowired private RandomUtils randomUtils;
    @Autowired private BookingsControllerApi bookingsControllerApi;
    private final ArrayList<Booking> bookings = new ArrayList<>();
    private final InheritableThreadLocal<ArrayList<Booking>> tlBookings = new InheritableThreadLocal<>();
    private final ArrayList<Booking> pickedBookings = new ArrayList<>();

    public Booking createInstance(Integer id, BookingData bookingData) {
        Booking booking = pick(new Booking(id, bookingData));
        this.bookings.add(booking);
        return booking;
    }

    public ArrayList<Booking> getAllBookings() {
        return bookings;
    }

    public BookingData generateRandomValidBookingData() {
        LocalDate checkInDate = randomUtils.generateRandomFutureDate();
        return BookingData.builder()
                .firstname(randomUtils.generateRandomString())
                .lastname(randomUtils.generateRandomString())
                .totalPrice(randomUtils.getRandomNumberInRange(50, 150))
                .isDepositPaid(true)
                .checkIn(checkInDate)
                .checkOut(randomUtils.generateRandomDateAfterDate(checkInDate))
                .additionalNeeds(randomUtils.generateRandomString())
                .build();
    }

    public void deleteAllCreatedBookings(UserSession adminSession) {
        log.info("Bookings to delete: " + getAllBookings());
        getAllBookings().forEach(booking -> deleteBooking(adminSession, booking));
        this.bookings.removeAll(getAllBookings());
    }

    private void deleteBooking(UserSession adminSession, Booking booking) {
        try {
            bookingsControllerApi.deleteBookingById(adminSession, booking.getId());
        } catch (UndeclaredThrowableException exception) {
            log.info("Connection refused: " + exception);
        }
    }

    public synchronized List<Booking> getNotPickedBookings() {
        return getAllBookings().stream()
                .filter(booking -> !pickedBookings.contains(booking))
                .collect(Collectors.toList());
    }

    public synchronized Booking pick(Booking booking) throws InstanceAlreadyPickedException {
        if (!isPicked(booking)) {
            this.pickedBookings.add(booking);
            if (this.tlBookings.get() == null) {
                this.tlBookings.set(new ArrayList<>());
                this.tlBookings.get().add(booking);
            } else if (!tlBookings.get().contains(booking)) {
                this.tlBookings.get().add(booking);
            }
        }
        return booking;
    }

    public boolean isPicked(Booking booking) {
        return this.pickedBookings.contains(booking);
    }

    public synchronized void unpickThreadBooking(Booking booking) {
        if (this.tlBookings.get() != null) {
            this.pickedBookings.remove(booking);
            this.tlBookings.get().remove(booking);
        }
    }

    public synchronized void unpickThreadBookings() {
        if (this.tlBookings.get() != null) {
            this.pickedBookings.removeAll(this.tlBookings.get());
            this.tlBookings.get().clear();
        }
    }
}

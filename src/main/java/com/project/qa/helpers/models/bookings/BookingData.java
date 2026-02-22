package com.project.qa.helpers.models.bookings;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class BookingData {
    private String firstname;
    private String lastname;
    private Integer totalPrice;
    private Boolean isDepositPaid;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String additionalNeeds;

    public String getCheckInDateString() {
        return convertToZonedDateString(this.checkIn);
    }

    public String getCheckOutDateString() {
        return convertToZonedDateString(this.checkOut);
    }

    private String convertToZonedDateString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public void update(BookingData newBookingData) {
        if (newBookingData.firstname != null) {
            this.firstname = newBookingData.firstname;
        }
        if (newBookingData.lastname != null) {
            this.lastname = newBookingData.lastname;
        }
        if (newBookingData.totalPrice != null) {
            this.totalPrice = newBookingData.totalPrice;
        }
        if (newBookingData.isDepositPaid != null) {
            this.isDepositPaid = newBookingData.isDepositPaid;
        }
        if (newBookingData.checkIn != null) {
            this.checkIn = newBookingData.checkIn;
        }
        if (newBookingData.checkOut != null) {
            this.checkOut = newBookingData.checkOut;
        }
        if (newBookingData.additionalNeeds != null) {
            this.additionalNeeds = newBookingData.additionalNeeds;
        }
    }

    public BookingData getCopy() {
        return BookingData.builder()
                .firstname(this.firstname)
                .lastname(this.lastname)
                .totalPrice(this.totalPrice)
                .isDepositPaid(this.isDepositPaid)
                .checkIn(this.checkIn)
                .checkOut(this.checkOut)
                .additionalNeeds(this.additionalNeeds)
                .build();
    }
}

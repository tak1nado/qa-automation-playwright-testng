package com.project.qa.helpers.models.bookings;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Booking {
    private int id;
    private BookingData bookingData;

    public void update(BookingData newBookingData) {
        if (newBookingData != null) {
            this.bookingData.update(newBookingData);
        }
    }

}

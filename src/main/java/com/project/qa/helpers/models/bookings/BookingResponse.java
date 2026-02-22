package com.project.qa.helpers.models.bookings;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    @JsonProperty("bookingid")
    private Integer bookingid;
    @JsonProperty("booking")
    private BookingDataResponse bookingDataResponse;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingDataResponse {
        @JsonProperty("firstname")
        private String firstname;
        @JsonProperty("lastname")
        private String lastname;
        @JsonProperty("totalprice")
        private Integer totalprice;
        @JsonProperty("depositpaid")
        private Boolean depositpaid;
        @JsonProperty("additionalneeds")
        private String additionalneeds;
        @JsonProperty("bookingdates")
        private BookingDatesResponse bookingDates;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BookingDatesResponse {
            @JsonFormat(pattern = "yyyy-MM-dd")
            private LocalDate checkin;
            @JsonFormat(pattern = "yyyy-MM-dd")
            private LocalDate checkout;
        }
    }
}
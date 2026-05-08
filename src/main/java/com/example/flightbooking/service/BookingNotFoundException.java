package com.example.flightbooking.service;

import java.util.UUID;

public class BookingNotFoundException extends RuntimeException {
  private final UUID bookingId;

  public BookingNotFoundException(UUID bookingId) {
    super("Booking not found: " + bookingId);
    this.bookingId = bookingId;
  }

  public UUID getBookingId() {
    return bookingId;
  }
}


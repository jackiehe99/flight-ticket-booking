package com.example.flightbooking.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookingRequest(
    @NotBlank @Size(max = 20) String flightNumber,
    @NotBlank @Size(max = 120) String passengerName,
    @Min(1) @Max(10) Integer seats) {
  public int seatsOrDefault() {
    return (seats == null) ? 1 : seats;
  }
}


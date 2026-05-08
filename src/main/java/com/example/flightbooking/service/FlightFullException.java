package com.example.flightbooking.service;

public class FlightFullException extends RuntimeException {
  private final String flightNumber;

  public FlightFullException(String flightNumber) {
    super("Flight is fully booked: " + flightNumber);
    this.flightNumber = flightNumber;
  }

  public String getFlightNumber() {
    return flightNumber;
  }
}


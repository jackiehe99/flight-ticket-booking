package com.example.flightbooking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "flights",
    uniqueConstraints = {@UniqueConstraint(name = "uq_flights_flight_number", columnNames = "flightNumber")})
public class Flight {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 20)
  private String flightNumber;

  @Column(nullable = false)
  private int capacity;

  @Column(nullable = false)
  private int bookedSeats;

  protected Flight() {}

  public Flight(String flightNumber, int capacity) {
    this.flightNumber = flightNumber;
    this.capacity = capacity;
    this.bookedSeats = 0;
  }

  public Long getId() {
    return id;
  }

  public String getFlightNumber() {
    return flightNumber;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getBookedSeats() {
    return bookedSeats;
  }

  public boolean hasAvailability(int seatsRequested) {
    return seatsRequested > 0 && bookedSeats + seatsRequested <= capacity;
  }

  public void reserve(int seatsRequested) {
    if (!hasAvailability(seatsRequested)) {
      throw new IllegalStateException("Flight is full");
    }
    this.bookedSeats += seatsRequested;
  }

  public void release(int seatsToRelease) {
    if (seatsToRelease <= 0) {
      return;
    }
    this.bookedSeats = Math.max(0, this.bookedSeats - seatsToRelease);
  }
}


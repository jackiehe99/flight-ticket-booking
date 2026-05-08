package com.example.flightbooking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {
  public enum Status {
    CONFIRMED,
    CANCELLED
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 20)
  private String flightNumber;

  @Column(nullable = false, length = 120)
  private String passengerName;

  @Column(nullable = false)
  private int seats;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Status status;

  @Column(nullable = false)
  private Instant createdAt;

  protected Booking() {}

  public Booking(String flightNumber, String passengerName, int seats) {
    this.flightNumber = flightNumber;
    this.passengerName = passengerName;
    this.seats = seats;
    this.status = Status.CONFIRMED;
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getFlightNumber() {
    return flightNumber;
  }

  public String getPassengerName() {
    return passengerName;
  }

  public int getSeats() {
    return seats;
  }

  public Status getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public boolean isActive() {
    return status == Status.CONFIRMED;
  }

  public void cancel() {
    this.status = Status.CANCELLED;
  }

  public void updatePassengerName(String passengerName) {
    this.passengerName = passengerName;
  }
}


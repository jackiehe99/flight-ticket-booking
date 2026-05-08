package com.example.flightbooking.api;

import java.time.Instant;
import java.util.UUID;

public record BookingDetailsResponse(
    UUID id,
    String flightNumber,
    String passengerName,
    int seats,
    String status,
    Instant createdAt) {}


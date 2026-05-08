package com.example.flightbooking.api;

import java.util.UUID;

public record BookingResponse(UUID bookingId, String status) {}


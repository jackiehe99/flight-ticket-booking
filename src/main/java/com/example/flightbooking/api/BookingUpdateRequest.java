package com.example.flightbooking.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookingUpdateRequest(@NotBlank @Size(max = 120) String passengerName) {}


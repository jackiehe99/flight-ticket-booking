package com.example.flightbooking.service;

public class IdempotencyKeyConflictException extends RuntimeException {
  public IdempotencyKeyConflictException(String message) {
    super(message);
  }
}


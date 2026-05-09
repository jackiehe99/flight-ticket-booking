package com.example.flightbooking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_records")
public class IdempotencyRecord {
  @Id
  @Column(nullable = false, length = 80)
  private String idempotencyKey;

  @Column(nullable = false, length = 64)
  private String requestHash;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = true)
  private UUID bookingId;

  protected IdempotencyRecord() {}

  public IdempotencyRecord(String idempotencyKey, String requestHash) {
    this.idempotencyKey = idempotencyKey;
    this.requestHash = requestHash;
    this.createdAt = Instant.now();
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public String getRequestHash() {
    return requestHash;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public UUID getBookingId() {
    return bookingId;
  }

  public boolean isCompleted() {
    return bookingId != null;
  }

  public void complete(UUID bookingId) {
    this.bookingId = bookingId;
  }
}


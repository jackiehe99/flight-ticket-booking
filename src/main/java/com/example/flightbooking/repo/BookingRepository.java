package com.example.flightbooking.repo;

import com.example.flightbooking.domain.Booking;
import com.example.flightbooking.domain.Booking.Status;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
  List<Booking> findAllByStatus(Status status);

  List<Booking> findAllByFlightNumber(String flightNumber);

  List<Booking> findAllByFlightNumberAndStatus(String flightNumber, Status status);
}


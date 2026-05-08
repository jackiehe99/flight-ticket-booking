package com.example.flightbooking.repo;

import com.example.flightbooking.domain.Flight;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface FlightRepository extends JpaRepository<Flight, Long> {
  Optional<Flight> findByFlightNumber(String flightNumber);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select f from Flight f where f.flightNumber = :flightNumber")
  Optional<Flight> findByFlightNumberForUpdate(@Param("flightNumber") String flightNumber);
}


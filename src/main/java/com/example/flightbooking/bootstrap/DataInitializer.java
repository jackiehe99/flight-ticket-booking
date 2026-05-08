package com.example.flightbooking.bootstrap;

import com.example.flightbooking.domain.Flight;
import com.example.flightbooking.repo.FlightRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationRunner {
  private final FlightRepository flightRepository;

  public DataInitializer(FlightRepository flightRepository) {
    this.flightRepository = flightRepository;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    seedIfMissing("AA100", 2);
    seedIfMissing("BA200", 3);
    seedIfMissing("CX300", 1);
  }

  private void seedIfMissing(String flightNumber, int capacity) {
    if (flightRepository.findByFlightNumber(flightNumber).isPresent()) {
      return;
    }
    flightRepository.save(new Flight(flightNumber, capacity));
  }
}


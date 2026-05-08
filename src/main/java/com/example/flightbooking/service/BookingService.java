package com.example.flightbooking.service;

import com.example.flightbooking.domain.Booking;
import com.example.flightbooking.domain.Flight;
import com.example.flightbooking.repo.BookingRepository;
import com.example.flightbooking.repo.FlightRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
  private final FlightRepository flightRepository;
  private final BookingRepository bookingRepository;

  public BookingService(FlightRepository flightRepository, BookingRepository bookingRepository) {
    this.flightRepository = flightRepository;
    this.bookingRepository = bookingRepository;
  }

  @Transactional
  public Booking createBooking(String flightNumber, String passengerName, int seatsRequested) {
    Flight flight =
        flightRepository
            .findByFlightNumberForUpdate(flightNumber)
            .orElseThrow(() -> new FlightNotFoundException(flightNumber));

    if (!flight.hasAvailability(seatsRequested)) {
      throw new FlightFullException(flightNumber);
    }

    flight.reserve(seatsRequested);
    Booking booking = bookingRepository.save(new Booking(flightNumber, passengerName, seatsRequested));
    return booking;
  }

  @Transactional
  public void cancelBooking(UUID bookingId) {
    Booking booking =
        bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));

    if (!booking.isActive()) {
      return;
    }

    Flight flight =
        flightRepository
            .findByFlightNumberForUpdate(booking.getFlightNumber())
            .orElseThrow(() -> new FlightNotFoundException(booking.getFlightNumber()));

    booking.cancel();
    flight.release(booking.getSeats());
  }

  @Transactional(readOnly = true)
  public Booking getBooking(UUID bookingId) {
    return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
  }

  @Transactional(readOnly = true)
  public List<Booking> listBookings(Optional<String> flightNumber, Optional<Booking.Status> status) {
    if (flightNumber.isPresent() && status.isPresent()) {
      return bookingRepository.findAllByFlightNumberAndStatus(flightNumber.get(), status.get());
    }
    if (flightNumber.isPresent()) {
      return bookingRepository.findAllByFlightNumber(flightNumber.get());
    }
    if (status.isPresent()) {
      return bookingRepository.findAllByStatus(status.get());
    }
    return bookingRepository.findAll();
  }
}


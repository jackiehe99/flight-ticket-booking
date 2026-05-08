package com.example.flightbooking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.flightbooking.domain.Booking;
import com.example.flightbooking.domain.Flight;
import com.example.flightbooking.repo.BookingRepository;
import com.example.flightbooking.repo.FlightRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
  @Mock private FlightRepository flightRepository;
  @Mock private BookingRepository bookingRepository;

  @InjectMocks private BookingService bookingService;

  private Flight flight;

  @BeforeEach
  void setUp() {
    flight = new Flight("AA100", 2);
  }

  @Test
  void createBooking_reservesSeats_andCreatesBooking() {
    when(flightRepository.findByFlightNumberForUpdate("AA100")).thenReturn(Optional.of(flight));
    when(bookingRepository.save(any(Booking.class)))
        .thenAnswer(
            inv -> {
              Booking b = inv.getArgument(0, Booking.class);
              assertNotNull(b.getCreatedAt());
              return b;
            });

    Booking booking = bookingService.createBooking("AA100", "Jane Doe", 2);

    assertEquals("AA100", booking.getFlightNumber());
    assertEquals("Jane Doe", booking.getPassengerName());
    assertEquals(2, booking.getSeats());
    assertEquals(2, flight.getBookedSeats());
  }

  @Test
  void createBooking_whenNoAvailability_throws_andDoesNotCreateBooking() {
    when(flightRepository.findByFlightNumberForUpdate("AA100")).thenReturn(Optional.of(flight));
    flight.reserve(2);

    assertThrows(FlightFullException.class, () -> bookingService.createBooking("AA100", "Jane Doe", 1));
    verify(bookingRepository, never()).save(any());
  }

  @Test
  void updatePassengerName_updatesOnlyName() {
    UUID id = UUID.randomUUID();
    Booking booking = new Booking("AA100", "Old Name", 1);
    when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

    Booking updated = bookingService.updatePassengerName(id, "New Name");

    assertEquals("New Name", updated.getPassengerName());
    assertEquals("AA100", updated.getFlightNumber());
    assertEquals(1, updated.getSeats());
  }

  @Test
  void cancelBooking_releasesSeats() {
    UUID id = UUID.randomUUID();
    when(flightRepository.findByFlightNumberForUpdate("AA100")).thenReturn(Optional.of(flight));

    // Create a booking first (so it is CONFIRMED and reserves a seat)
    when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0, Booking.class));
    Booking created = bookingService.createBooking("AA100", "Jane Doe", 1);

    // Then cancel it
    when(bookingRepository.findById(id)).thenReturn(Optional.of(created));
    bookingService.cancelBooking(id);

    assertEquals(0, flight.getBookedSeats());
    verify(flightRepository).findByFlightNumberForUpdate(eq("AA100"));
  }
}


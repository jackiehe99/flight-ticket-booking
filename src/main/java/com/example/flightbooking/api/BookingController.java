package com.example.flightbooking.api;

import com.example.flightbooking.domain.Booking;
import com.example.flightbooking.service.BookingService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping
  public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
    Booking booking =
        bookingService.createBooking(
            request.flightNumber(), request.passengerName(), request.seatsOrDefault());

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(booking.getId())
            .toUri();

    return ResponseEntity.created(location)
        .body(new BookingResponse(booking.getId(), booking.getStatus().name()));
  }

  @DeleteMapping("/{bookingId}")
  public ResponseEntity<Void> cancel(@PathVariable UUID bookingId) {
    bookingService.cancelBooking(bookingId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{bookingId}")
  public ResponseEntity<BookingDetailsResponse> get(@PathVariable UUID bookingId) {
    Booking booking = bookingService.getBooking(bookingId);
    return ResponseEntity.ok(
        new BookingDetailsResponse(
            booking.getId(),
            booking.getFlightNumber(),
            booking.getPassengerName(),
            booking.getSeats(),
            booking.getStatus().name(),
            booking.getCreatedAt()));
  }

  @GetMapping
  public ResponseEntity<List<BookingDetailsResponse>> list(
      @RequestParam(name = "flightNumber", required = false) Optional<String> flightNumber,
      @RequestParam(name = "status", required = false) Optional<Booking.Status> status) {
    List<BookingDetailsResponse> body =
        bookingService.listBookings(flightNumber, status).stream()
            .map(
                b ->
                    new BookingDetailsResponse(
                        b.getId(),
                        b.getFlightNumber(),
                        b.getPassengerName(),
                        b.getSeats(),
                        b.getStatus().name(),
                        b.getCreatedAt()))
            .toList();
    return ResponseEntity.ok(body);
  }
}


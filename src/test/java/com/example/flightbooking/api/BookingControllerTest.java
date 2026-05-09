package com.example.flightbooking.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.flightbooking.domain.Booking;
import com.example.flightbooking.service.BookingNotFoundException;
import com.example.flightbooking.service.BookingService;
import com.example.flightbooking.service.FlightFullException;
import com.example.flightbooking.service.FlightNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BookingController.class)
@Import(RestExceptionHandler.class)
class BookingControllerTest {
  @Autowired private MockMvc mvc;

  @MockBean private BookingService bookingService;

  @Test
  void createBooking_returns201_andLocationHeader() throws Exception {
    UUID id = UUID.randomUUID();
    Booking booking = new Booking("AA100", "Jane Doe", 1);
    // Booking entity generates id internally; for this MVC test just ensure response contains something.
    ReflectionTestUtils.setField(booking, "id", id);
    when(bookingService.createBooking(eq("AA100"), eq("Jane Doe"), eq(1))).thenReturn(booking);

    mvc.perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"flightNumber\":\"AA100\",\"passengerName\":\"Jane Doe\",\"seats\":1}"))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.bookingId").value(id.toString()))
        .andExpect(jsonPath("$.status").value("CONFIRMED"));
  }

  @Test
  void createBooking_whenFlightMissing_returns404() throws Exception {
    when(bookingService.createBooking(anyString(), anyString(), anyInt()))
        .thenThrow(new FlightNotFoundException("ZZ999"));

    mvc.perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"flightNumber\":\"ZZ999\",\"passengerName\":\"Jane Doe\",\"seats\":1}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("FLIGHT_NOT_FOUND"));
  }

  @Test
  void createBooking_whenFull_returns409() throws Exception {
    when(bookingService.createBooking(anyString(), anyString(), anyInt()))
        .thenThrow(new FlightFullException("AA100"));

    mvc.perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"flightNumber\":\"AA100\",\"passengerName\":\"Jane Doe\",\"seats\":1}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("FLIGHT_FULL"));
  }

  @Test
  void getBooking_whenMissing_returns404() throws Exception {
    UUID id = UUID.randomUUID();
    when(bookingService.getBooking(id)).thenThrow(new BookingNotFoundException(id));

    mvc.perform(get("/api/bookings/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void listBookings_invalidStatus_returns400() throws Exception {
    mvc.perform(get("/api/bookings").queryParam("status", "INVALID")).andExpect(status().isBadRequest());
  }

  @Test
  void patchBooking_whenMissing_returns404() throws Exception {
    UUID id = UUID.randomUUID();
    when(bookingService.updatePassengerName(eq(id), anyString())).thenThrow(new BookingNotFoundException(id));

    mvc.perform(
            patch("/api/bookings/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"passengerName\":\"New Name\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteBooking_returns204() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(bookingService).cancelBooking(id);

    mvc.perform(delete("/api/bookings/{id}", id)).andExpect(status().isNoContent());
  }

  @Test
  void listBookings_returns200() throws Exception {
    when(bookingService.listBookings(any(), any())).thenReturn(List.of());
    mvc.perform(get("/api/bookings")).andExpect(status().isOk());
  }
}


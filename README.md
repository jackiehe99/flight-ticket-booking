# Flight Ticket Booking API (Spring Boot)

Minimal Flight Ticket Booking API. The client provides the `flightNumber` (no search / destination logic).

## Requirements

- Java 17+
- Maven 3.9+ (make sure `mvn` is on your PATH)

## How to run

```bash
mvn spring-boot:run
```

Service starts on `http://localhost:8080`.

## How to run tests

```bash
mvn test
```

On startup, the app seeds a few flights:

- `AA100` capacity 2
- `BA200` capacity 3
- `CX300` capacity 1

## Booking API

### Create booking

`POST /api/bookings`

- **201 Created**: booking confirmed
- **200 OK**: idempotent replay (same `Idempotency-Key` used again)
- **404 Not Found**: flight number does not exist
- **409 Conflict**: flight is full (prevents overbooking)
- **409 Conflict**: `Idempotency-Key` reused with different request payload
- **400 Bad Request**: validation error

Example:

```bash
curl -i -X POST "http://localhost:8080/api/bookings" -H "Content-Type: application/json" -d "{\"flightNumber\":\"AA100\",\"passengerName\":\"Jane Doe\",\"seats\":1}"
```

### Cancel booking (optional)

`DELETE /api/bookings/{bookingId}`

- **204 No Content**: cancelled (or was already cancelled)
- **404 Not Found**: booking id does not exist

Example:

```bash
curl -i -X DELETE "http://localhost:8080/api/bookings/00000000-0000-0000-0000-000000000000"
```

### Get booking details

`GET /api/bookings/{bookingId}`

- **200 OK**: returns booking status/details
- **404 Not Found**: booking id does not exist

Example:

```bash
curl -i "http://localhost:8080/api/bookings/00000000-0000-0000-0000-000000000000"
```

### List bookings (simple filters)

`GET /api/bookings`

- **200 OK**: returns bookings (possibly empty)
- **400 Bad Request**: invalid filter value (e.g. `status=INVALID`)

Optional query params:

- `status=CONFIRMED|CANCELLED` (case-insensitive)
- `flightNumber=AA100`

Examples:

```bash
curl -i "http://localhost:8080/api/bookings"
curl -i "http://localhost:8080/api/bookings?status=CONFIRMED"
curl -i "http://localhost:8080/api/bookings?flightNumber=AA100&status=CONFIRMED"
```

### Update booking (mutable fields only)

`PATCH /api/bookings/{bookingId}`

- **200 OK**: updated booking
- **404 Not Found**: booking id does not exist
- **400 Bad Request**: validation error

Currently supported mutable fields:

- `passengerName`

Example:

```bash
curl -i -X PATCH "http://localhost:8080/api/bookings/00000000-0000-0000-0000-000000000000" -H "Content-Type: application/json" -d "{\"passengerName\":\"Janet Doe\"}"
```
Manual Improvements:
Improved unit test "createBooking_returns201_andLocationHeader()" in BookingControllerTest.java. Reason: it was returning a Booking with a null id (because in real life JPA assigns it, but in the test the service is mocked) so I set the private id via ReflectionTestUtils and now assert $.bookingId equals that UUID.

Fixed unit test "cancelBooking_releasesSeats()" in BookingServiceTest.java. Reason: it was failing and I wanted it to be more clean and maintainable. The test originally mixed "create" and "cancel" logic so I updated it to not call createBooking().

Made the status query param parsing case-insensitive for GET /api/bookings. Reason: to improve client usability and robustness. 

Started implementing an idempotency key for POST /api/bookings. Reason: to prevent duplicate bookings when clients retry the same request.

Enabled the H2 console in the browser and also turn on SQL logging to the console. Reason: to make the app obserable while developing.

Further improvments:
Complete idempotent booking in BookingService.java and BookingController.java for the new idempotent solution
Add consistent ordering for GET /api/bookings
Add unit tests for case-insensitive query param parsing, idempotency replay/conflict
Add a integration test proving “no overbooking under concurrency”
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

On startup, the app seeds a few flights:

- `AA100` capacity 2
- `BA200` capacity 3
- `CX300` capacity 1

## Booking API

### Create booking

`POST /api/bookings`

- **201 Created**: booking confirmed
- **404 Not Found**: flight number does not exist
- **409 Conflict**: flight is full (prevents overbooking)
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


// GuestCapacityException.java
package com.hotel.booking.exception;

/**
 * KASTAS NÄR FÖR MÅNGA GÄSTER FÖR RUMSTYPEN
 * Används i BookingService.createBooking() när numberOfGuests > roomType.capacity
 * Hanteras av GlobalExceptionHandler → returnerar 400 BAD REQUEST
 */
public class GuestCapacityException extends RuntimeException {
    public GuestCapacityException(String roomType, int maxCapacity, int requestedGuests) {
        super("Rumstypen " + roomType + " har plats för max " + maxCapacity + " gäster. Du försökte boka för " + requestedGuests + " gäster.");
    }
}
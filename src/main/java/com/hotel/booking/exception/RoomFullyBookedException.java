// RoomFullyBookedException.java
package com.hotel.booking.exception;

/**
 * KASTAS NÄR INGA FLER RUM AV EN TYP FINNS LEDIGA
 * Används i BookingService.createBooking() när availableRooms.get(roomType) <= 0
 * Hanteras av GlobalExceptionHandler → returnerar 409 CONFLICT
 */
public class RoomFullyBookedException extends RuntimeException {
    public RoomFullyBookedException(String roomType) {
        super("Inga " + roomType + " kvar. Alla rum av denna typ är fullbokade.");
    }
}
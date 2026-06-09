// BookingNotFoundException.java
package com.hotel.booking.exception;

/**
 * KASTAS NÄR BOKNING INTE HITTAS
 * Används i BookingService.deleteBooking() när booking med angivet ID inte finns
 * Hanteras av GlobalExceptionHandler → returnerar 404 NOT FOUND
 */
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(int id) {
        super("Bokning med ID " + id + " hittades inte.");
    }
}
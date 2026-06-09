package com.hotel.booking.dto;

import jakarta.validation.constraints.*;

/**
 * BOOKING REQUEST DTO (Data Transfer Object)

 * DTO används för att ta emot data från klienten (POST /api/bookings)
 * Skillnad mot Booking-modellen: DTO har INTE id, totalPrice etc.

 * @ Valid annoteringen i controllern triggar alla valideringsregler nedan
 */
public class BookingRequest {

    @NotBlank(message = "Namn får inte vara tomt")  // Får inte vara null, tom eller bara mellanslag
    @Pattern(regexp = "^[A-Öa-ö\\s]+$", message = "Namn får bara innehålla bokstäver och mellanslag")
    private String guestName;  // Gästens namn - endast bokstäver och mellanslag

    @NotBlank(message = "Rumstyp krävs")
    @Pattern(regexp = "^(Enkelrum|Dubbelrum|Svit)$", message = "Rumstyp måste vara Enkelrum, Dubbelrum eller Svit")
    private String roomType;   // Rumstyp som STRÄNG (konverteras till enum i controllern)

    @Min(value = 1, message = "Minst 1 gäst")    // Minimum 1
    @Max(value = 3, message = "Max 3 gäster")    // Maximum 3 (hotellets policy)
    private int numberOfGuests;  // Antal gäster

    // Getters och Setters - BEHÖVS för att Spring ska kunna mappa JSON → objekt
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }
}
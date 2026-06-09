package com.hotel.booking.model;

import jakarta.validation.constraints.*;

/**
 * BOOKING MODELL - Representerar en bokning i systemet

 * Detta är DOMÄN-modellen (affärsobjektet) som används internt
 * Skillnad mot DTO: Booking har ID, totalPrice och använder RoomType ENUM
 */
public class Booking {
    private static int counter = 1;  // Statisk räknare för unika ID (ökar automatiskt)
    private int id;                   // Unikt ID för bokningen

    @NotBlank(message = "Namn får inte vara tomt")
    @Pattern(regexp = "^[A-Öa-ö\\s]+$", message = "Namn får bara innehålla bokstäver och mellanslag")
    private String guestName;

    @NotNull(message = "Rumstyp krävs")
    private RoomType roomType;        // ANVÄNDER ENUM istället för String (typsäkert!)

    @Min(value = 1, message = "Minst 1 gäst")
    @Max(value = 3, message = "Max 3 gäster")
    private int numberOfGuests;

    private int totalPrice;           // Totalpris (prisPerNatt * antal nätter - här bara 1 natt)

    /**
     * KONSTRUKTOR för att skapa en NY bokning
     * - Sätter automatiskt ID (statisk counter ökar)
     * - Beräknar totalPrice från rummets pris per natt
     *
     * @param guestName - Gästens namn
     * @param roomType - Rumstyp (enum)
     * @param numberOfGuests - Antal gäster
     */
    public Booking(String guestName, RoomType roomType, int numberOfGuests) {
        this.id = counter++;                     // Tilldela ID och öka räknaren
        this.guestName = guestName;
        this.roomType = roomType;
        this.numberOfGuests = numberOfGuests;
        this.totalPrice = roomType.pricePerNight; // Endast en natt (förenklat)
    }

    // Default constructor för deserialisering (Jackson behöver denna)
    public Booking() {}

    // Getters och Setters - ALLA fält måste ha dessa för JSON-serialisering
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }
}